package core.contest_project.community.comment_like.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class CommentLikeCreator {
    private final CommentLikeRepository commentLikeRepository;

    public void create(Long commentId, Long userId) {
        commentLikeRepository.save(commentId, userId);
    }


}
