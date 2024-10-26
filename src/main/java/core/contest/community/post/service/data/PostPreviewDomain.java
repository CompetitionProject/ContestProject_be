package core.contest.community.post.service.data;

import core.contest.community.file.service.data.FileDomain;
import core.contest.community.post.service.data.PostInfo;
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
    private FileDomain thumbnail;


    public String getThumbnailUrl() {
        if(thumbnail != null) {
            return thumbnail.getUrl();
        }
        return null;
    }

}
