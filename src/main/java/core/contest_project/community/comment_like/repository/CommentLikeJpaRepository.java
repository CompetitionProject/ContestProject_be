package core.contest_project.community.comment_like.repository;


import core.contest_project.community.comment_like.entity.CommentLike;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CommentLikeJpaRepository extends JpaRepository<CommentLike, Long> {
    boolean existsByComment_IdAndUser_Id(Long CommentId, Long userId);
    boolean existsByComment_Id(Long CommentId);


    @Modifying
    @Query("delete from CommentLike cl where cl.comment.id=:commentId and cl.user.id=:userId")
    void deleteByComment_IdAndUser_Id(@Param("commentId")Long CommentId, @Param("userId")Long userId);

    @Modifying
    @Query("delete from CommentLike cl where cl.comment.id=:commentId")
    void deleteAllByComment_Id(@Param("commentId")Long CommentId);

    @Modifying
    @Query("delete from CommentLike cl where cl.comment.id in :commentIds")
    void deleteAllByCommentIds(@Param("commentIds") List<Long> commentIds);

    void deleteAllByUser_Id(Long userId);

    long countByComment_Id(Long CommentId);
}
