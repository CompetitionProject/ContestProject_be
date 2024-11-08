package core.contest_project.community.comment_like.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class CommentLikeDeleter {
    private final CommentLikeRepository commentLikeRepository;

    public void remove(Long commentId, Long userId) {
        commentLikeRepository.delete(commentId, userId);
    }

    public void removeAll(Long commentId){
        commentLikeRepository.deleteAll(commentId);
    }

    /**
     * @param commentIds : n(댓글) ?? n(댓글 좋아요)
     *
     */
    public void removeAll(List<Long> commentIds) {
        commentLikeRepository.deleteAll(commentIds);
    }


}
