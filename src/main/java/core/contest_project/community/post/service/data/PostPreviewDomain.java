package core.contest_project.community.post.service.data;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class PostPreviewDomain {
    private Long postId;

    private PostInfo info;

    private String writerNickname;

    private Long likeCount;
    private Long viewCount;
    private Long commentCount;

    private LocalDateTime createAt;
    private String thumbnailUrl;



}
