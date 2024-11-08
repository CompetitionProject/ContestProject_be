package core.contest_project.community.comment.dto;

public record CreateCommentRequest(
        String content,
        boolean isReply,
        Long parentId,
        Boolean isAnonymous
) {
}
