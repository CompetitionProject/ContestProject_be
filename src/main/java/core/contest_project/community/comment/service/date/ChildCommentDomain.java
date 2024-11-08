package core.contest_project.community.comment.service.date;


import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;


@Getter
@Builder
@AllArgsConstructor
public class ChildCommentDomain {
    @JsonProperty("replyCommentId")
    private Long id;
    private CommentInfo info;
    private LocalDateTime createAt;
    private boolean isDeleted;

    private boolean isWriter;
    private Long commentLikeCount;
    private boolean isLiked;


}

