package core.contest_project.community.comment.service.date;

import core.contest_project.community.comment_like.service.CommentLikeDomain;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Builder
@AllArgsConstructor
public class CommentDomain {
    private Long id;
    private CommentInfo info;
    private LocalDateTime createAt;
    private boolean isDelete;
    private boolean isLiked;
    private boolean isWriter;
    private List<CommentLikeDomain> likes;

    public void setLiked(boolean liked) {
        isLiked = liked;
    }

    public void setWriter(boolean writer) {
        isWriter = writer;
    }

    public void anonymize(){
        info.anonymize();
    }
}
