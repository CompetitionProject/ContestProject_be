package core.contest_project.community.comment.service.date;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class CommentActivityDomain {
    private Long id;
    private String title;
    private String content;
}
