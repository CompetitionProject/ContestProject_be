package core.contest_project.community.post.dto.response;

import core.contest_project.community.post.service.data.PostPreviewDomain;

import java.time.LocalDateTime;

public record PostPreviewResponse(
        Long postId,

        String writerNickname,

        String title,
        String content,

        Long likeCount,
        Long viewCount,
        Long commentCount,

        LocalDateTime createAt,

        String thumbnailUrl

) {
    public static PostPreviewResponse from(PostPreviewDomain domain){
        return new PostPreviewResponse(
                domain.getPostId(),
                domain.getWriterNickname(),
                domain.getInfo().title(),
                domain.getInfo().content(),
                domain.getLikeCount(),
                domain.getViewCount(),
                domain.getCommentCount(),
                domain.getCreateAt(),
                domain.getThumbnailUrl()
        );
    }
}
