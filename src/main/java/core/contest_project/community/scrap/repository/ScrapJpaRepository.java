package core.contest_project.community.scrap.repository;


import core.contest_project.community.scrap.entity.Scrap;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ScrapJpaRepository extends JpaRepository<Scrap, Long> {
    boolean existsByPost_IdAndUser_Id(Long postId, Long userId);
    boolean existsByPost_Id(Long postId);





    @Modifying
    @Query("delete from Scrap s where s.post.id=:postId and s.user.id=:userId")
    void deleteByPost_IdAndUser_Id(@Param("postId")Long postId, @Param("userId")Long userId);

    @Modifying
    @Query("delete from Scrap s where s.post.id=:postId")
    void deleteAllByPostId(@Param("postId")Long postId);

    void deleteAllByUser_Id(Long userId);

    long countByPost_Id(Long postId);
}
