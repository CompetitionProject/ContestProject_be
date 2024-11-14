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
        boolean isBookmarked

){
        public static ContestSimpleResponse from(Contest contest, boolean isBookmarked) {

                Long remainingDays = ChronoUnit.DAYS.between(
                        contest.getStartDate().toLocalDate(),
                        contest.getEndDate().toLocalDate()
                );

                // 마감일이 지나면 -1 고정
                if (remainingDays < 0) {
                        remainingDays = -1L;
                }

                // posterUrl 처리
                String posterUrl = null;
                if (contest.getPosterImage() != null && !contest.getPosterImage().isEmpty()) {
                        posterUrl = contest.getPosterImage().get(0).getUrl();  // 첫 번째 포스터의 URL
                }

                // contestFields를 String으로 변환
                List<String> fields = contest.getContestFields().stream()
                        .map(ContestField::getDescription)
                        .collect(Collectors.toList());

                return ContestSimpleResponse.builder()
                        .contestId(contest.getId())
                        .title(contest.getTitle())
                        .bookmarkCount(contest.getBookmarkCount())
                        .remainingDays(remainingDays)
                        .endDate(contest.getEndDate())
                        .posterUrl(posterUrl)
                        .postFields(fields)
                        .userId(contest.getWriter().getId())
                        .isBookmarked(isBookmarked)
                        .build();
        }
}
