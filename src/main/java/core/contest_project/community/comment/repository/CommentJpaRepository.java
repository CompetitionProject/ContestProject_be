package core.contest_project.community.comment.repository;

import core.contest_project.community.comment.entity.Comment;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface CommentJpaRepository extends JpaRepository<Comment, Long> {

    @Query(value="select c.anonymous_number from comment c where c.post_id=:postId and c.writer_id=:writerId limit 1", nativeQuery=true)
    Long findAnonymousCommentByPostIdAndWriterId(Long postId, Long writerId);


    @Query("select c from Comment c" +
            " join fetch c.writer" +
            " left join fetch c.parent" +
            " left join fetch c.likes" +
            " where c.id=:commentId")
    Optional<Comment> findByCommentId(@Param("commentId")Long commentId);


    @Query("select c from Comment c" +
            " left join fetch c.parent" +
            " left join fetch c.writer" +
            " left join fetch c.likes" +
            " where c.post.id=:postId" +
            " order by c.createAt asc")
    List<Comment> findAllByPostId(@Param("postId")Long postId);

    @Query("select c from Comment c" +
            " join fetch c.post p" +
            " join fetch p.writer" +
            " left join fetch c.likes cl" +
            " where c.writer.id=:userId" +
            " order by c.createAt desc")
    Slice<Comment> findAllByUserId(@Param("userId")Long userId, Pageable pageable);

    @Query("select c from Comment c" +
            " join fetch c.post" +
            " where c.writer.teamMemberCode=:teamMemberCode")
    Slice<Comment>findCommentsByTeamMemberCode(@Param("teamMemberCode")String teamMemberCode, Pageable pageable);


    @Modifying
    @Query("update Comment c set c.isDeleted=true, c.content='삭제된 댓글입니다.' where c.id=:commentId")
    void updateCommentToDeleted(@Param("commentId")Long commentId);

    @Modifying
    @Query("delete from Comment c where c.post.id=:postId")
    void deleteAllByPostId(@Param("postId") Long postId);

    @Modifying
    @Query("delete from Comment c where c.parent is not null and c.post.id=:postId")
    void deleteChildByPostId(@Param("postId")Long postId);

    boolean existsByParentId(Long parentId);

}
