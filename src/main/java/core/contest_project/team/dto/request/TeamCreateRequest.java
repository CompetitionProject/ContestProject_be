package core.contest_project.team.dto.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

public record TeamCreateRequest(
        @NotBlank(message = "팀 이름은 필수입니다")
        String name,
        @NotBlank(message = "팀 소개는 필수입니다")
        String description,

        @Min(value = 1, message = "최소 팀원 수는 1명입니다")
        @Max(value = 10, message = "최대 팀원 수는 10명입니다")
//        int maxMemberCount,

        String profileImageUrl
) {
}
