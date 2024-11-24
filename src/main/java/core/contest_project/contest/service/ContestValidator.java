package core.contest_project.contest.service;

import core.contest_project.common.error.contest.ContestErrorResult;
import core.contest_project.common.error.contest.ContestException;
import core.contest_project.common.error.file.FileErrorResult;
import core.contest_project.common.error.file.FileException;
import core.contest_project.contest.entity.Contest;
import core.contest_project.file.FileType;
import core.contest_project.file.service.FileRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;


@Component
@RequiredArgsConstructor
public class ContestValidator {
    private static final int MAX_CONTENT_IMAGES = 4;
    private static final int MAX_ATTACHMENTS = 2;
    public void validateDateRange(LocalDateTime startDate, LocalDateTime endDate) {
        if (startDate.isAfter(endDate)) {
            throw new ContestException(ContestErrorResult.INVALID_DATE_RANGE);
        }
    }

    public void validateFiles(List<FileRequest> files) {
        if (files == null || files.isEmpty()) {
            throw new FileException(FileErrorResult.EMPTY_FILE);
        }

        boolean hasImage = files.stream()
                .anyMatch(file -> file.type() == FileType.IMAGE);
        if (!hasImage) {
            throw new ContestException(ContestErrorResult.IMAGE_REQUIRED);
        }

        long contentImageCount = files.stream()
                .filter(file -> file.type() == FileType.IMAGE)
                .count();
        if (contentImageCount > MAX_CONTENT_IMAGES) {
            throw new FileException(FileErrorResult.EXCEED_MAX_CONTENT_IMAGES);
        }

        long attachmentCount = files.stream()
                .filter(file -> file.type() == FileType.ATTACHMENT)
                .count();
        if (attachmentCount > MAX_ATTACHMENTS) {
            throw new FileException(FileErrorResult.EXCEED_MAX_ATTACHMENTS);
        }
    }

    public void validateFiles(Contest contest, List<FileRequest> requestFiles) {
        boolean willHaveImage = requestFiles.stream()
                .anyMatch(file -> file.type() == FileType.IMAGE);

        if (!contest.hasPoster() && !willHaveImage) {
            throw new ContestException(ContestErrorResult.IMAGE_REQUIRED);
        }
    }
}
