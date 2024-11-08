package core.contest_project.community.comment.service;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class CommentUpdater {
    private final CommentRepository commentRepository;

    public void updateCommentStatusToDeleted(Long commentId) {
        commentRepository.updateCommentStatusToDeleted(commentId);
    }
}
