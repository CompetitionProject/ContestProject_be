package core.contest_project.contest.service;

import core.contest_project.bookmark.dto.BookmarkStatus;
import core.contest_project.bookmark.repository.BookmarkRepository;
import core.contest_project.bookmark.service.BookmarkService;
import core.contest_project.common.error.file.FileErrorResult;
import core.contest_project.common.error.file.FileException;
import core.contest_project.contest.dto.request.ContestUpdateRequest;
import core.contest_project.contest.dto.request.ContestCreateRequest;
import core.contest_project.contest.dto.response.ContestApplicationInfo;
import core.contest_project.contest.dto.response.ContestContentResponse;
import core.contest_project.contest.dto.response.ContestResponse;
import core.contest_project.contest.dto.response.ContestSimpleResponse;
import core.contest_project.contest.entity.Contest;
import core.contest_project.common.error.contest.ContestErrorResult;
import core.contest_project.common.error.contest.ContestException;
import core.contest_project.common.error.user.UserErrorResult;
import core.contest_project.common.error.user.UserException;
import core.contest_project.contest.entity.ContestField;
import core.contest_project.contest.entity.ContestSortOption;
import core.contest_project.contest.repository.ContestRepository;
import core.contest_project.file.FileType;
import core.contest_project.file.dto.SingleFileResponse;
import core.contest_project.file.entity.File;
import core.contest_project.file.service.FileServiceV2;
import core.contest_project.user.Role;
import core.contest_project.user.entity.User;
import core.contest_project.user.repository.UserJpaRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ContestService {

    private final FileServiceV2 fileService;
    private final UserJpaRepository userRepository;
    private final ContestRepository contestRepository;
    private final BookmarkService bookmarkService;
    private final BookmarkRepository bookmarkRepository;

    @Transactional
    public Long createContest(ContestCreateRequest request, Long userId) {
        validateRole(userId);
        User user = findUserByUserId(userId);

        Contest contest = Contest.createContest(
                request.title(),
                request.content(),
                request.startDate(),
                request.endDate(),
                request.qualification(),
                request.awardScale(),
                request.host(),
                request.applicationMethod(),
                request.applicationEmail(),
                request.hostUrl(),
                request.contestFields(),
                user
        );

        // 파일 분류 및 설정
        if (request.files() != null && !request.files().isEmpty()) {
            List<File> contentImages = request.files().stream()
                    .filter(file -> file.type() == FileType.IMAGE)
                    .map(file -> file.toEntity(contest))
                    .collect(Collectors.toList());
            contest.withContentImages(contentImages);

            List<File> attachments = request.files().stream()
                    .filter(file -> file.type() == FileType.ATTACHMENT)
                    .map(file -> file.toEntity(contest))
                    .collect(Collectors.toList());
            contest.withAttachments(attachments);

            // 포스터는 단일 파일이어야 함
            request.files().stream()
                    .filter(file -> file.type() == FileType.POSTER)
                    .findFirst()
                    .ifPresent(posterFile -> {
                        List<File> poster = List.of(posterFile.toEntity(contest));
                        contest.withPoster(poster);
                    });
        }

        Contest savedContest = contestRepository.save(contest);
        return savedContest.getId();
    }


    /*@Transactional
    public ContestResponse getContest(Long contestId, Long userId) {
        // 기본 정보 조회
        Contest contest = contestRepository.findByIdWithWriter(contestId)
                .orElseThrow(() -> new ContestException(ContestErrorResult.CONTEST_NOT_EXIST));

        // 각각의 파일 리스트 조회
        Contest contestWithContentImages = contestRepository.findByIdWithContentImages(contestId);
        Contest contestWithPosterImage = contestRepository.findByIdWithPosterImage(contestId);
        Contest contestWithAttachments = contestRepository.findByIdWithAttachments(contestId);

        // 북마크 여부 확인
        boolean isBookmarked = bookmarkRepository.existsByContestIdAndUserId(contestId, userId);

        incrementViewCountAsync(contest);

        return ContestResponse.builder()
                .contestId(contest.getId())
                .title(contest.getTitle())
                .content(contest.getContent())
                .contentImageUrls(contestWithContentImages.getContentImages().stream()
                        .map(this::mapToFileResponse)
                        .collect(Collectors.toList()))
                .posterUrl(contestWithPosterImage.getPosterImage().isEmpty() ? null :
                        mapToFileResponse(contestWithPosterImage.getPosterImage().get(0)))
                .attachmentUrls(contestWithAttachments.getAttachments().stream()
                        .map(this::mapToFileResponse)
                        .collect(Collectors.toList()))
                .viewCount(contest.getViewCount())
                .bookmarkCount(contest.getBookmarkCount())
                .remainingDays(calculateRemainingDays(contest.getEndDate()))
                .endDate(contest.getEndDate())
                .qualification(contest.getQualification())
                .awardScale(contest.getAwardScale())
                .host(contest.getHost())
                .applicationMethod(contest.getApplicationMethod().getDescription())
                .applicationEmail(contest.getApplicationEmail())
                .hostUrl(contest.getHostUrl())
                .contestFields(contest.getContestFields().stream()
                        .map(ContestField::getDescription)
                        .collect(Collectors.toList()))
                .contestStatus(contest.getContestStatus().name())
                .writerId(contest.getWriter().getId())
                .isBookmarked(isBookmarked)
                .build();
    }*/

    @Transactional
    public void updateContest(Long contestId, ContestUpdateRequest request, Long userId) {
        validateRole(userId);

        Contest contest = contestRepository.findById(contestId)
                .orElseThrow(() -> new ContestException(ContestErrorResult.CONTEST_NOT_EXIST));

        // 포스터 검증
        boolean willHavePoster = request.files().stream()
                .anyMatch(file -> file.type() == FileType.POSTER);

        // 현재 포스터가 없는 경우에만 새 포스터 필수
        if (!contest.hasPoster() && !willHavePoster) {
            throw new ContestException(ContestErrorResult.POSTER_REQUIRED);
        }

        contest.updateContest(
                request.title(),
                request.content(),
                request.startDate(),
                request.endDate(),
                request.qualification(),
                request.awardScale(),
                request.host(),
                request.applicationMethod(),
                request.applicationEmail(),
                request.hostUrl(),
                request.contestFields()
        );

        // 파일 분류 및 설정
        if (request.files() != null && !request.files().isEmpty()) {
            // 본문 이미지
            List<File> contentImages = request.files().stream()
                    .filter(file -> file.type() == FileType.IMAGE)
                    .map(file -> file.toEntity(contest))
                    .collect(Collectors.toList());
            contest.withContentImages(contentImages);

            // 첨부 파일
            List<File> attachments = request.files().stream()
                    .filter(file -> file.type() == FileType.ATTACHMENT)
                    .map(file -> file.toEntity(contest))
                    .collect(Collectors.toList());
            contest.withAttachments(attachments);

            // 포스터
            request.files().stream()
                    .filter(file -> file.type() == FileType.POSTER)
                    .findFirst()
                    .ifPresent(posterFile -> {
                        List<File> poster = List.of(posterFile.toEntity(contest));
                        contest.withPoster(poster);
                    });
        }
    }

    @Transactional
    public void deleteContest(Long contestId, Long userId) {
        Contest contest = contestRepository.findById(contestId)
                .orElseThrow(() -> new ContestException(ContestErrorResult.CONTEST_NOT_EXIST));

        validateRole(userId);

        // 모든 파일 ID 수집
        List<Long> fileIds = new ArrayList<>();

        fileIds.addAll(contest.getContentImages().stream()
                .map(File::getId)
                .collect(Collectors.toList()));

        fileIds.addAll(contest.getAttachments().stream()
                .map(File::getId)
                .collect(Collectors.toList()));

        // 포스터 이미지 처리
        if (contest.getPosterImage() != null && !contest.getPosterImage().isEmpty()) {
            fileIds.addAll(contest.getPosterImage().stream()
                    .map(File::getId)
                    .collect(Collectors.toList()));
        }

        // 파일 삭제
        if (!fileIds.isEmpty()) {
            fileService.deleteFilesByFileIds(fileIds);
        }

        // 공모전 삭제
        contestRepository.delete(contest);
    }

    @Transactional(readOnly = true)
    public Slice<ContestSimpleResponse> getContestsByField(
            List<ContestField> fields,
            Long lastContestId,
            int pageSize,
            Long userId,
            ContestSortOption sortOption
    ) {
        Pageable pageable = PageRequest.of(0, pageSize);
        // 관심 분야(기본값: 전체)
        List<ContestField> searchFields = (fields != null && !fields.isEmpty()) ? fields : null;
        // 정렬 옵션(기본값: 최신순)
        ContestSortOption finalSortOption = (sortOption != null) ? sortOption : ContestSortOption.LATEST;

        Slice<Contest> contests = contestRepository.findByContestFields(
                searchFields,
                lastContestId,
                finalSortOption.name(),
                pageable);

        List<Long> contestIds = contests.getContent().stream()
                .map(Contest::getId)
                .collect(Collectors.toList());
        List<Long> bookmarkedContestIds = bookmarkRepository.findBookmarkedContestIds(contestIds, userId);

        return contests.map(contest -> {
            boolean isBookmarked = bookmarkedContestIds.contains(contest.getId());
            return ContestSimpleResponse.from(contest, isBookmarked);
        });
    }

    @Transactional
    public BookmarkStatus toggleBookmark(Long contestId, Long userId) {
        Contest contest = contestRepository.findById(contestId)
                .orElseThrow(() -> new ContestException(ContestErrorResult.CONTEST_NOT_EXIST));
        // bookmarkService에서도 contestRepository.findById를 하는데

        BookmarkStatus status = bookmarkService.toggleBookmark(contest, userId);
        if (status.equals(BookmarkStatus.BOOKMARK)) {
            contest.incrementBookmarkCount();
        } else {
            contest.decrementBookmarkCount();
        }
        contestRepository.save(contest);
        return status;
    }

    public ContestApplicationInfo getApplicationInfo(Long contestId) {
        return null;
    }

    private User findUserByUserId(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new UserException(UserErrorResult.USER_NOT_EXIST));
    }

    private void validateRole(Long userId) {
        User user = findUserByUserId(userId);
        if (!user.getRole().equals(Role.ADMIN)) {
            throw new ContestException(ContestErrorResult.UNAUTHORIZED_ACTION);
        }
    }

    private void validateContentImagesCount(List<MultipartFile> images) {
        if (images.size() > 4) {
            throw new FileException(FileErrorResult.EXCEED_MAX_CONTENT_IMAGES);
        }
    }

    private void validateAttachmentsCount(List<MultipartFile> attachments) {
        if (attachments.size() > 2) {
            throw new FileException(FileErrorResult.EXCEED_MAX_ATTACHMENTS);
        }
    }

    private void validatePosterCount(Contest contest) {
        if (contest.getPosterImage() != null && !contest.getPosterImage().isEmpty()) {
            throw new FileException(FileErrorResult.POSTER_ALREADY_EXISTS);
        }
    }

    private SingleFileResponse mapToFileResponse(File file) {
        return new SingleFileResponse(file.getId(), file.getStoreName());
    }

    private Long calculateRemainingDays(LocalDateTime endDate) {
        Long remainingDays = ChronoUnit.DAYS.between(LocalDateTime.now(), endDate);
        return remainingDays < 0 ? -1L : remainingDays;
    }

    @Async
    @Transactional
    protected void incrementViewCountAsync(Contest contest) {
        contestRepository.incrementViewCount(contest.getId());
    }


    @Transactional
    public ContestResponse getContestInfo(Long contestId, Long userId) {
        // 기본 정보와 writer 조회
        Contest contest = contestRepository.findByIdWithWriter(contestId)
                .orElseThrow(() -> new ContestException(ContestErrorResult.CONTEST_NOT_EXIST));

        // 북마크 여부 확인
        boolean isBookmarked = bookmarkRepository.existsByContestIdAndUserId(contestId, userId);

        // 조회수 증가 (비동기)
        incrementViewCountAsync(contest);

        return ContestResponse.from(contest, isBookmarked);
    }

    @Transactional(readOnly = true)
    public ContestContentResponse getContestContent(Long contestId, Long userId) {
        // 기본 정보와 content만 조회
        Contest contest = contestRepository.findByIdWithContent(contestId)
                .orElseThrow(() -> new ContestException(ContestErrorResult.CONTEST_NOT_EXIST));

        // 이미지와 첨부파일 별도 조회
        Contest contestWithImages = contestRepository.findByIdWithContentImages(contestId);
        Contest contestWithAttachments = contestRepository.findByIdWithAttachments(contestId);

        return ContestContentResponse.builder()
                .contestId(contest.getId())
                .content(contest.getContent())
                .contentImageUrls(contestWithImages.getContentImages().stream()
                        .map(image -> new SingleFileResponse(image.getId(), image.getStoreName()))
                        .collect(Collectors.toList()))
                .attachmentUrls(contestWithAttachments.getAttachments().stream()
                        .map(attachment -> new SingleFileResponse(attachment.getId(), attachment.getStoreName()))
                        .collect(Collectors.toList()))
                .build();
    }
}
