package core.contest_project.file.service;

import core.contest_project.file.FileType;

public record FileResponse(
        String url,
        String uploadFilename,
        FileType type
) {
}
