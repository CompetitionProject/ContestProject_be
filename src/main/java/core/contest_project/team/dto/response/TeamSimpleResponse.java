package core.contest_project.team.dto.response;

import core.contest_project.team.entity.Team;
import lombok.Builder;

@Builder
public record TeamSimpleResponse(
        Long teamId,
        String teamName
) {
    public static TeamSimpleResponse from(Team team) {
        return TeamSimpleResponse.builder()
                .teamId(team.getId())
                .teamName(team.getName())
                .build();
    }
}
