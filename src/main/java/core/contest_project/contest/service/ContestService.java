package core.contest_project.contest.service;

import core.contest_project.awaiter.individual.repository.IndividualAwaiterRepository;
import core.contest_project.awaiter.team.repository.TeamAwaiterRepository;
import core.contest_project.bookmark.dto.BookmarkStatus;
import core.contest_project.bookmark.repository.BookmarkRepository;
import core.contest_project.bookmark.service.BookmarkService;
import core.contest_project.common.error.contest.ContestErrorResult;
import core.contest_project.common.error.contest.ContestException;
import core.contest_project.contest.dto.request.ContestCreateRequest;
import core.contest_project.contest.dto.request.ContestUpdateRequest;
import core.contest_project.contest.dto.response.ContestApplicationInfo;
import core.contest_project.contest.dto.response.ContestContentResponse;
import core.contest_project.contest.dto.response.ContestResponse;
import core.contest_project.contest.dto.response.ContestSimpleResponse;
import core.contest_project.contest.entity.Contest;
import core.contest_project.contest.entity.ContestField;
import core.contest_project.contest.entity.ContestSortOption;
import core.contest_project.contest.entity.ContestStatus;
import core.contest_project.contest.repository.ContestRepository;
import core.contest_project.file.FileLocation;
import core.contest_project.file.FileType;
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
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
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

        // 이미지 1개 이상
        boolean willHaveImage = request.files().stream()
                .anyMatch(file -> file.type() == FileType.IMAGE);

        // 현재 이미지가 없고, 새로 추가되는 이미지도 없는 경우
        if (!contest.hasPoster() && !willHaveImage) {
            throw new ContestException(ContestErrorResult.IMAGE_REQUIRED);
        }

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

    @Transactional(readOnly = true)
    public Slice<ContestSimpleResponse> getContestsByField(
            List<ContestField> fields,
            Long lastContestId,
            int pageSize,
            UserDomain user,
            ContestSortOption sortOption
    ) {
        // 검색 필드
        List<ContestField> searchFields = (fields != null && !fields.isEmpty()) ? fields : null;
        // 정렬
        ContestSortOption finalSortOption = (sortOption != null) ? sortOption : ContestSortOption.LATEST;
        // 활성 상태(모집 중) 공모전만 조회하도록 상태 리스트 생성
        List<ContestStatus> activeStatuses = Arrays.asList(ContestStatus.NOT_STARTED, ContestStatus.IN_PROGRESS);

        Pageable pageable = PageRequest.of(0, pageSize + 1);

        Slice<Object[]> results = contestRepository.findByContestFields(
                searchFields,
                lastContestId,
                finalSortOption.name(),
                activeStatuses,
                pageable
        );

        boolean hasNext = results.getContent().size() > pageSize;
        List<Object[]> content = hasNext ?
                results.getContent().subList(0, pageSize) :
                results.getContent();

        // Contest 목록과 대기자 수
        List<Contest> contests = content.stream()
                .map(result -> (Contest) result[0])
                .toList();

        Map<Long, Long> awaiterCountMap = content.stream()
                .collect(Collectors.toMap(
                        result -> ((Contest) result[0]).getId(),
                        result -> (Long) result[1]
                ));

        // 북마크 정보 조회
        List<Long> contestIds = contests.stream()
                .map(Contest::getId)
                .toList();
        List<Long> bookmarkedContestIds = bookmarkRepository.findBookmarkedContestIds(contestIds, user.getId());

        // 응답 생성
        List<ContestSimpleResponse> responses = contests.stream()
                .map(contest -> {
                    boolean isBookmarked = bookmarkedContestIds.contains(contest.getId());
                    Long awaiterCount = awaiterCountMap.get(contest.getId());
                    return ContestSimpleResponse.from(contest, isBookmarked, awaiterCount);
                })
                .toList();

        return new SliceImpl<>(responses, pageable, hasNext);
    }

    @Transactional
    public BookmarkStatus toggleBookmark(Long contestId, UserDomain user) {
        Contest contest = findContestById(contestId);

        BookmarkStatus status = bookmarkService.toggleBookmark(contest, user.getId());
        if (status.equals(BookmarkStatus.BOOKMARK)) {
            contest.incrementBookmarkCount();
        } else {
            contest.decrementBookmarkCount();
        }
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

}
