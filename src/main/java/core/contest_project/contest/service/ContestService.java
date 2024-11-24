package core.contest_project.contest.service;

import core.contest_project.awaiter.individual.repository.IndividualAwaiterRepository;
import core.contest_project.awaiter.team.repository.TeamAwaiterRepository;
import core.contest_project.bookmark.dto.BookmarkStatus;
import core.contest_project.bookmark.repository.BookmarkRepository;
import core.contest_project.bookmark.service.BookmarkService;
import core.contest_project.common.error.contest.ContestErrorResult;
import core.contest_project.common.error.contest.ContestException;
import core.contest_project.contest.dto.request.ContestCreateRequest;
import core.contest_project.contest.dto.request.ContestCursor;
import core.contest_project.contest.dto.request.ContestUpdateRequest;
import core.contest_project.contest.dto.response.*;
import core.contest_project.contest.entity.Contest;
import core.contest_project.contest.entity.ContestField;
import core.contest_project.contest.entity.ContestSortOption;
import core.contest_project.contest.entity.ContestStatus;
import core.contest_project.contest.repository.ContestRepository;
import core.contest_project.file.FileLocation;
import core.contest_project.file.FileUtil;
import core.contest_project.file.entity.File;
import core.contest_project.file.service.db.FileCreator;
import core.contest_project.file.service.db.FileDeleter;
import core.contest_project.file.service.db.FileUpdater;
import core.contest_project.user.entity.User;
import core.contest_project.user.service.UserValidator;
import core.contest_project.user.service.data.UserDomain;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.*;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ContestService {

    private final ContestRepository contestRepository;
    private final BookmarkService bookmarkService;
    private final BookmarkRepository bookmarkRepository;
    private final FileCreator fileCreator;
    private final FileUpdater fileUpdater;
    private final FileDeleter fileDeleter;
    private final UserValidator userValidator;
    private final ContestValidator contestValidator;
    private final IndividualAwaiterRepository individualAwaiterRepository;
    private final TeamAwaiterRepository teamAwaiterRepository;

    private static final int PAGE_SIZE = 20;


    @Transactional
    public Long createContest(ContestCreateRequest request, UserDomain user) {
        userValidator.validateAdmin(user);
        contestValidator.validateDateRange(request.startDate(), request.endDate());
        contestValidator.validateFiles(request.files());

        Contest contest = Contest.createContest(request, User.from(user));
        Contest savedContest = contestRepository.save(contest);

        List<File> files = FileUtil.toEntity(request.files(), FileLocation.CONTEST);
        fileCreator.saveAll(savedContest.getId(), files);

        return savedContest.getId();
    }

    @Transactional
    public void updateContest(Long contestId, ContestUpdateRequest request, UserDomain user) {
        userValidator.validateAdmin(user);

        Contest contest = findContestById(contestId);
        contestValidator.validateDateRange(request.startDate(), request.endDate());
        contestValidator.validateFiles(contest, request.files());

        Contest updatedContest = contest.updateContest(request);
        contestRepository.save(updatedContest);

        List<File> files = FileUtil.toEntity(request.files(), FileLocation.CONTEST);
        fileUpdater.update(contestId, FileLocation.CONTEST, files);
    }


    @Transactional
    public void deleteContest(Long contestId, UserDomain user) {
        Contest contest = findContestById(contestId);

        userValidator.validateAdmin(user); // 관리자 권한
        bookmarkRepository.deleteAllByContestId(contestId);
        individualAwaiterRepository.deleteAllByContestId(contestId); // 개인 대기자 삭제
        teamAwaiterRepository.deleteAllByContestId(contestId); // 팀 대기자 삭제
        //s3에서는 삭제x
        fileDeleter.deleteByContestId(contestId);
        contestRepository.delete(contest);
    }

    /*@Transactional(readOnly = true)
    public ContestPageResponse getContestsByField(
            List<ContestField> fields,
            ContestCursor cursor,
            ContestSortOption sortOption,
            UserDomain user
    ) {
        // 검색 필드
        List<ContestField> searchFields = (fields != null && !fields.isEmpty()) ? fields : null;
        // 정렬
        ContestSortOption finalSortOption = (sortOption != null) ? sortOption : ContestSortOption.LATEST;
        // 활성 상태(모집 중) 공모전만 조회
        List<ContestStatus> activeStatuses = Arrays.asList(ContestStatus.NOT_STARTED, ContestStatus.IN_PROGRESS);

        // 커서 값 추출
        Long cursorId = cursor != null ? cursor.contestId() : null;
        LocalDateTime cursorDateTime = null;
        Long cursorBookmarkCount = null;
        LocalDateTime cursorEndDate = null;
        Long cursorAwaiterCount = null;
        Long cursorReviewCount = null;

        if (cursor != null) {
            if (cursor.sortValue() instanceof ContestCursor.SortValue.Latest latest) {
                cursorDateTime = latest.createdAt();
            } else if (cursor.sortValue() instanceof ContestCursor.SortValue.Bookmarks bookmarks) {
                cursorBookmarkCount = bookmarks.count();
            } else if (cursor.sortValue() instanceof ContestCursor.SortValue.Deadline deadline) {
                cursorEndDate = deadline.endDate();
            } else if (cursor.sortValue() instanceof ContestCursor.SortValue.Awaiters awaiters) {
                cursorAwaiterCount = awaiters.count();
            } else if (cursor.sortValue() instanceof ContestCursor.SortValue.Reviews reviews) {
                cursorReviewCount = reviews.count();
            }
        }

        // 페이지 요청 생성 (size + 1로 다음 페이지 존재 여부 확인)
        PageRequest pageRequest = PageRequest.of(0, PAGE_SIZE + 1);

        // 데이터 조회
        List<Object[]> results = contestRepository.findByContestFields(
                searchFields,
                cursor,
                cursorId,
                cursorDateTime,
                cursorBookmarkCount,
                cursorEndDate,
                cursorAwaiterCount,
                cursorReviewCount,
                finalSortOption.name(),
                activeStatuses,
                pageRequest
        );

        // 다음 페이지 존재 여부 확인
        boolean hasNext = results.size() > PAGE_SIZE;
        List<Object[]> content = hasNext ?
                results.subList(0, PAGE_SIZE) :
                results;

        // Contest 목록과 부가 정보 매핑
        List<Contest> contests = new ArrayList<>();
        Map<Long, Long> awaiterCountMap = new HashMap<>();
        Map<Long, Long> reviewCountMap = new HashMap<>();

        for (Object[] result : content) {
            Contest contest = (Contest) result[0];
            Long awaiterCount = (Long) result[1];
            Long reviewCount = (Long) result[2];
            contests.add(contest);
            awaiterCountMap.put(contest.getId(), awaiterCount);
            reviewCountMap.put(contest.getId(), reviewCount);
        }

        // 북마크 정보 조회
        List<Long> contestIds = contests.stream()
                .map(Contest::getId)
                .toList();
        List<Long> bookmarkedContestIds = bookmarkRepository.findBookmarkedContestIds(contestIds, user.getId());

        // 응답 데이터 생성
        List<ContestSimpleResponse> responses = contests.stream()
                .map(contest -> {
                    boolean isBookmarked = bookmarkedContestIds.contains(contest.getId());
                    Long awaiterCount = awaiterCountMap.get(contest.getId());
                    return ContestSimpleResponse.from(contest, isBookmarked, awaiterCount);
                })
                .toList();

        // 다음 커서 생성
        String nextCursor = null;
        if (hasNext && !contests.isEmpty()) {
            Contest lastContest = contests.get(contests.size() - 1);
            ContestCursor nextContestCursor = ContestCursor.create(
                    lastContest,
                    finalSortOption,
                    awaiterCountMap.get(lastContest.getId()),
                    reviewCountMap.get(lastContest.getId())
            );
            nextCursor = nextContestCursor.encode();
        }

        return new ContestPageResponse(responses, hasNext, nextCursor);
    }*/

    @Transactional
    public BookmarkStatus toggleBookmark(Long contestId, UserDomain user) {
        Contest contest = findContestById(contestId);

        BookmarkStatus status = bookmarkService.toggleBookmark(contest, user.getId());
        updateBookmarkCount(status, contest);
        contestRepository.save(contest);
        return status;
    }

    @Transactional(readOnly = true)
    public ContestApplicationInfo getApplicationInfo(Long contestId) {
        Contest contest = findContestById(contestId);
        return ContestApplicationInfo.from(contest);
    }

    @Transactional
    public ContestResponse getContestInfo(Long contestId, UserDomain user) {
        // 기본 정보와 writer 조회
        Contest contest = contestRepository.findByIdWithWriter(contestId)
                .orElseThrow(() -> new ContestException(ContestErrorResult.CONTEST_NOT_EXIST));

        incrementViewCountAsync(contestId);

        // 북마크 여부 확인
        boolean isBookmarked = bookmarkRepository.existsByContestIdAndUserId(contestId, user.getId());
        return ContestResponse.from(contest, isBookmarked);
    }

    @Transactional(readOnly = true)
    public ContestContentResponse getContestContent(Long contestId) {
        Contest contestWithImages = contestRepository.findByIdWithContentImages(contestId)
                .orElseThrow(() -> new ContestException(ContestErrorResult.CONTEST_NOT_EXIST));
        Contest contestWithAttachments = contestRepository.findByIdWithAttachments(contestId)
                .orElseThrow(() -> new ContestException(ContestErrorResult.CONTEST_NOT_EXIST));

        return ContestContentResponse.from(contestWithImages, contestWithAttachments);
    }

    public Contest findContestById(Long contestId) {
        return contestRepository.findById(contestId)
                .orElseThrow(() -> new ContestException(ContestErrorResult.CONTEST_NOT_EXIST));
    }

    @Async
    @Transactional
    public void incrementViewCountAsync(Long contestId) {
        contestRepository.findById(contestId).ifPresent(contest -> {
            contest.incrementViewCount();
            contestRepository.save(contest);
        });
    }

    private void updateBookmarkCount(BookmarkStatus status, Contest contest) {
        if (status.equals(BookmarkStatus.BOOKMARK)) {
            contest.incrementBookmarkCount();
        } else {
            contest.decrementBookmarkCount();
        }
    }
}
