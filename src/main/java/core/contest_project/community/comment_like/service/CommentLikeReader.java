package core.contest_project.community.comment_like.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class CommentLikeReader {
    private final CommentLikeRepository commentLikeRepository;

    public boolean isLiked(Long commentId, Long userId) {
        return commentLikeRepository.isLiked(commentId, userId);

    }

    public boolean isLiked(Long commentId){
        return commentLikeRepository.isLiked(commentId);
    }

    public Long count(Long commentId) {
        return commentLikeRepository.count(commentId);
    }
}
