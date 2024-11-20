package core.contest_project.contest.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import core.contest_project.contest.entity.Contest;
import core.contest_project.contest.entity.ContestField;
import lombok.Builder;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Collectors;

@Builder
public record ContestResponse(
        Long contestId,
        String title,

        String posterUrl,
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


                Long remainingDays = ChronoUnit.DAYS.between(LocalDateTime.now(), contest.getEndDate());
                if (remainingDays < 0) {
                        remainingDays = -1L;
                }

                return ContestResponse.builder()
                        .contestId(contest.getId())
                        .title(contest.getTitle())
                        .posterUrl(contest.getPosterUrl())
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
