package core.contest_project.team.dto.response;

import lombok.Builder;

@Builder
public record TeamBriefProfileResponse(
        Long teamId,
        String name,
        String description
) {
}
