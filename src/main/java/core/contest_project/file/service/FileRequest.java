package core.contest_project.file.service;

import core.contest_project.file.FileType;

public record FileRequest(
        String url,
        String uploadFilename,
        FileType Type
) {
}
