package core.contest_project.community.post_like.service;

public interface PostLikeRepository {
    boolean isLiked(Long postId, Long userId);

    Long count(Long postId);

    void delete(PostLikeDomain postLikeDomain);
    void delete(Long postId, Long userId);
    void deleteAll(Long postId);
    void deleteALlByUserId(Long userId);

    void save(PostLikeDomain postLikeDomain);
    void save(Long postId, Long userId);
}
