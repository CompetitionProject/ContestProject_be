package core.contest_project.contest.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import core.contest_project.contest.entity.Contest;
import core.contest_project.contest.entity.ContestField;
import core.contest_project.file.dto.SingleFileResponse;
import lombok.Builder;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Collectors;

@Builder
public record ContestResponse(
        Long contestId,
        String title,
//        String content,
//        List<SingleFileResponse> contentImageUrls,
        SingleFileResponse posterUrl,
//        List<SingleFileResponse> attachmentUrls,
        Long viewCount,
        Long bookmarkCount,
        boolean isBookmarked,
        Long remainingDays,
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy.MM.dd HH:mm:ss")
        LocalDateTime endDate,

        String qualification,

        String awardScale,

        String host,

        String applicationMethod,
        String applicationEmail,

        String hostUrl,

        List<String> contestFields,
        String contestStatus,

        Long writerId

){
        public static ContestResponse from(Contest contest, boolean isBookmarked) {
                SingleFileResponse posterUrl = contest.getPosterImage() != null && !contest.getPosterImage().isEmpty()
                        ? new SingleFileResponse(contest.getPosterImage().get(0).getId(), contest.getPosterImage().get(0).getStoreName())
                        : null;

                Long remainingDays = ChronoUnit.DAYS.between(LocalDateTime.now(), contest.getEndDate());
                if (remainingDays < 0) {
                        remainingDays = -1L;
                }

                return ContestResponse.builder()
                        .contestId(contest.getId())
                        .title(contest.getTitle())
//                        .content(contest.getContent())
//                        .contentImageUrls(contest.getContentImages().stream()
//                                .map(image -> new SingleFileResponse(image.getId(), image.getStoreName()))
//                                .collect(Collectors.toList()))
                        .posterUrl(posterUrl)
//                        .attachmentUrls(contest.getAttachments().stream()
//                                .map(attachment -> new SingleFileResponse(attachment.getId(), attachment.getStoreName()))
//                                .collect(Collectors.toList()))
                        .viewCount(contest.getViewCount())
                        .bookmarkCount(contest.getBookmarkCount())
                        .isBookmarked(isBookmarked)
                        .remainingDays(remainingDays)
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
                        .build();
        }
}
