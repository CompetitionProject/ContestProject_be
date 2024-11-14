package core.contest_project.file.dto;

import lombok.Builder;

import java.util.List;

@Builder
public record FileUrlsResponse(
        List<String> urls
) {
    public static FileUrlsResponse from(List<String> urls) {
        return FileUrlsResponse.builder()
                .urls(urls)
                .build();
    }

}
