package core.contest_project.community.comment_like.service;

import java.util.List;

public interface CommentLikeRepository {
    boolean isLiked(Long commentId, Long userId);
    boolean isLiked(Long commentId);

    Long count(Long commentId);

    void delete(Long commentId, Long userId);
    void deleteAll(List<Long> commentIds);
    void deleteAll(Long commentId);
    void deleteAllByUserId(Long userId);

    void save(CommentLikeDomain commentLikeDomain);
    void save(Long commentId, Long userId);
}
