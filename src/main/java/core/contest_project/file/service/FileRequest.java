package core.contest_project.file.service;

import core.contest_project.contest.entity.Contest;
import core.contest_project.file.FileLocation;
import core.contest_project.file.FileType;
import core.contest_project.file.entity.File;

import java.time.LocalDateTime;

public record FileRequest(
        String url,
        String uploadFilename,
        FileType type
) {
    public File toEntity(Contest contest) {
        return File.builder()
                .contest(contest)
                .url(url)
                .uploadName(uploadFilename)
                .fileType(type)
                .location(FileLocation.CONTEST)
                .createAt(LocalDateTime.now())
                .build();
    }
}
