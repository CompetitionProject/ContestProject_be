package core.contest_project.community.comment.dto;

import java.util.List;

public record CommentResponse(
        List<ParentCommentResponse> comments
) {
}
