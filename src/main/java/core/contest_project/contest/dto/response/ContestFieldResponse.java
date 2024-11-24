package core.contest_project.contest.dto.response;

import core.contest_project.contest.entity.ContestField;

public record ContestFieldResponse(
        String name,
        String description
) {
    public static ContestFieldResponse from(ContestField field) {
        return new ContestFieldResponse(field.name(), field.getDescription());
    }
}
