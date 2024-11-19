package core.contest_project.team.dto.request;

import jakarta.validation.constraints.NotNull;

public record LeaderTransferRequest(
        @NotNull(message = "새로운 팀장 ID는 필수입니다")
        Long newLeaderId
) {
}
