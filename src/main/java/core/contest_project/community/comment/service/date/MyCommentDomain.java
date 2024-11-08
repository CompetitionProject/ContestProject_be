package core.contest_project.community.comment.service.date;

import core.contest_project.community.file.service.data.FileDomain;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

import static lombok.AccessLevel.PROTECTED;

@Getter
@Builder
@AllArgsConstructor(access=PROTECTED)
public class MyCommentDomain {
    private Long id;
    private String content;
    private Long likeCount;

    private Long postId;
    private String postTitle;
    private String postWriterNickname;
    private Long postViewCount;
    private LocalDateTime postCreatedAt;
    private FileDomain thumbnail;

    public String getThumbnailUrl() {
        if(thumbnail != null) {
            return thumbnail.getUrl();
        }
        return null;
    }
}
