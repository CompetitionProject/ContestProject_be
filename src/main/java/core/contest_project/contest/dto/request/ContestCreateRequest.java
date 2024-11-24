package core.contest_project.contest.dto.request;

import core.contest_project.contest.entity.ContestApplicationMethod;
import core.contest_project.contest.entity.ContestField;
import core.contest_project.file.FileType;
import core.contest_project.file.service.FileRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;

import java.time.LocalDateTime;
import java.util.List;


public record ContestCreateRequest(
        @NotBlank(message = "제목은 필수입니다.")
        String title,
        @NotBlank(message = "내용은 필수입니다")
        String content,
        @NotNull(message = "이미지는 1장 이상입니다.")
        @Valid
        List<FileRequest> files,


        LocalDateTime startDate,

        LocalDateTime endDate,
        String qualification,
        String awardScale,
        @NotBlank(message = "주최사는 필수입니다")
        String host,

        @NotNull(message = "지원 방법은 필수입니다")
        ContestApplicationMethod applicationMethod,

        @Email(message = "올바른 이메일 형식이 아닙니다")
        String applicationEmail,
        String hostUrl,
        List<ContestField> contestFields
) {
}
