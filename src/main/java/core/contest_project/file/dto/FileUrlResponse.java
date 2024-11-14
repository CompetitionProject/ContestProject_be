package core.contest_project.file.dto;

import lombok.Builder;

@Builder
public record FileUrlResponse(
        String url
) {
    public static FileUrlResponse from(String url) {
        return FileUrlResponse.builder()
                .url(url)
                .build();
    }
}
