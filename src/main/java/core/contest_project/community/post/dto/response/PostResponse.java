package core.contest_project.community.post.dto.response;

import core.contest_project.community.post.service.data.PostDomain;
import core.contest_project.file.service.FileResponse;

import java.time.LocalDateTime;
import java.util.List;

public record PostResponse(
        Long postId,

        Long writerId,
        String writerNickname,
        String writerProfileUrl,
        boolean isWriter,

        String title,
        String contestTitle,
        String content,

        Long viewCount,

        LocalDateTime createdAt,

        Long scrapCount,
        boolean isScraped,

        Long likeCount,
        boolean isLiked,

        List<FileResponse> files
) {

    public static PostResponse from(PostDomain domain){
        return new PostResponse(
                domain.getId(),
                domain.getWriter().getId(),
                domain.getWriter().getUserInfo().getNickname(),
                domain.getWriter().getUserInfo().getSnsProfileImageUrl(),
                domain.isWriter(),
                domain.getInfo().title(),
                domain.getInfo().contestTitle(),
                domain.getInfo().content(),
                domain.getViewCount(),
                domain.getCreateAt(),
                domain.getScrapCount(),
                domain.isScraped(),
                domain.getLikeCount(),
                domain.isLiked(),
                domain.getFiles()
        );

    }
}
