package core.contest_project.bookmark.dto;

import lombok.Builder;

@Builder
public record BookmarkStatusResponse(
        BookmarkStatus status
) {
    public static BookmarkStatusResponse from(BookmarkStatus status) {
        return BookmarkStatusResponse.builder()
                .status(status)
                .build();
    }

}
