package core.contest_project.community.post_like.repository;


import core.contest_project.community.post_like.entity.PostLike;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface PostLikeJpaRepository extends JpaRepository<PostLike, Long> {
    boolean existsByPost_IdAndUser_Id(Long postId, Long userId);
    boolean existsByPost_Id(Long postId);



    @Modifying
    @Query("delete from PostLike pl where pl.post.id=:postId and pl.user.id=:userId")
    void deleteByPost_IdAndUser_Id(@Param("postId")Long postId, @Param("userId")Long userId);


    @Modifying
    @Query("delete from PostLike pl where pl.post.id=:postId")
    void deleteAllByPostId(@Param("postId")Long postId);

    void deleteAllByUser_Id(Long userId);

    long countByPost_Id(Long postId);
}
