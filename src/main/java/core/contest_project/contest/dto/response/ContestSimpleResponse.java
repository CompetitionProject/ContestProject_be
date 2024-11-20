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
public record ContestSimpleResponse (
        Long contestId,
        String title,
        Long bookmarkCount,
        Long remainingDays,

        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy.MM.dd HH:mm:ss")
        LocalDateTime endDate,

        String posterUrl,

        List<String> postFields,
        Long userId,
        boolean isBookmarked,
        Long awaiterCount

){
        public static ContestSimpleResponse from(Contest contest, boolean isBookmarked, Long awaiterCount) {
                Long remainingDays = ChronoUnit.DAYS.between(LocalDateTime.now(), contest.getEndDate());
                if (remainingDays < 0) {
                        remainingDays = -1L;
                }

                List<String> fields = contest.getContestFields().stream()
                        .map(ContestField::getDescription)
                        .collect(Collectors.toList());

                return ContestSimpleResponse.builder()
                        .contestId(contest.getId())
                        .title(contest.getTitle())
                        .bookmarkCount(contest.getBookmarkCount())
                        .remainingDays(remainingDays)
                        .endDate(contest.getEndDate())
                        .posterUrl(contest.getPosterUrl())
                        .postFields(fields)
                        .isBookmarked(isBookmarked)
                        .awaiterCount(awaiterCount)  // 대기자 수 설정
                        .build();
        }
}
