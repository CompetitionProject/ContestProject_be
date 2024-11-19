package core.contest_project.team.dto.response;

import core.contest_project.team.entity.Team;
import lombok.Builder;

import java.time.LocalDateTime;
import java.util.List;

@Builder
public record TeamProfileResponse(
        Long teamId,
        String teamName,
        TeamImageInfo imageInfo,
        LocalDateTime createdAt
) {
    public static TeamProfileResponse from(Team team, List<String> memberProfileUrls) {
        return new TeamProfileResponse(
                team.getId(),
                team.getName(),
                team.getProfileImageUrl() != null
                        ? TeamImageInfo.TeamImage.from(team.getProfileImageUrl())
                        : TeamImageInfo.MemberImages.from(memberProfileUrls),
                team.getCreatedAt()
        );
    }
}
