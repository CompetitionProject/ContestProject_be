package core.contest_project.contest.dto.request;

import core.contest_project.contest.entity.ContestApplicationMethod;
import core.contest_project.contest.entity.ContestField;
import core.contest_project.file.FileType;
import core.contest_project.file.service.FileRequest;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;
import java.util.List;


public record ContestCreateRequest(
        String title,
        String content,
        @NotNull(message = "포스터 이미지는 필수입니다.")
        List<FileRequest> files,
        LocalDateTime startDate,
        LocalDateTime endDate,
        String qualification,
        String awardScale,
        String host,
        ContestApplicationMethod applicationMethod,
        @Email
        String applicationEmail,
        String hostUrl,
        List<ContestField> contestFields
) {
        @AssertTrue(message = "포스터 이미지는 필수입니다")
        private boolean isPosterPresent() {
                return files != null &&
                        files.stream()
                                .anyMatch(file -> file.type() == FileType.POSTER);
        }
}
