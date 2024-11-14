package core.contest_project.contest.dto.request;

import core.contest_project.contest.entity.ContestApplicationMethod;
import core.contest_project.contest.entity.ContestField;
import core.contest_project.file.service.FileRequest;

import java.time.LocalDateTime;
import java.util.List;


public record ContestUpdateRequest(
        String title,
        String content,
        List<FileRequest> files,
        LocalDateTime startDate,
        LocalDateTime endDate,
        String qualification,
        String awardScale,
        String host,
        ContestApplicationMethod applicationMethod,
        String applicationEmail,
        String hostUrl,
        List<ContestField> contestFields,
        List<String> deletedFileUrls
) {

}
