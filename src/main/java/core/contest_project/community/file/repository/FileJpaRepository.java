package core.contest_project.community.file.repository;

import core.contest_project.community.file.entity.Contest;
import core.contest_project.community.post.entity.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface FileJpaRepository extends JpaRepository<core.contest_project.community.file.entity.File,Long> {
    List<core.contest_project.community.file.entity.File> findAllByPostIdOrderByOrderIndex(Long postId);

    @Query("select file from File file where file.post is null and file.createAt < :yesterday")
    List<core.contest_project.community.file.entity.File> findOrphanFiles(@Param("yesterday") LocalDateTime yesterday);


    @Query("select file from File file where file.post.id=:postId and file.fileType='IMAGE' order by file.orderIndex limit 1")
    Optional<core.contest_project.community.file.entity.File> findThumbnail(Long postId);

    @Modifying
    @Query("update File file set file.post=:post, file.orderIndex=:order where file.storeName =:storeName")
    void updateAllByPost(@Param("post") Post post, @Param("order") Long order, @Param("storeName")String storeName);

    @Modifying
    @Query("update File file set file.contest=:contest where file.storeName =:storeName")
    void updateAllByContest(@Param("contest") Contest contest, @Param("storeName")String storeName);


    @Modifying
    @Query("delete from File file where file.post.id=:postId and file.storeName in :storeFileNames")
    void deleteAllByPostId(@Param("postId")Long postId, @Param("storeFileNames")List<String> storeFileNames);

    @Modifying
    @Query("delete from File file where file.storeName in :storeFileNames")
    void deleteAllByStoreFileName(@Param("storeFileNames")List<String> storeFileNames);

    @Modifying
    @Query("delete from File file where file.id in :ids")
    void deleteAll(@Param("ids")List<Long> ids);

    @Modifying
    @Query("delete from File file where file.post.id =:postId")
    void delete(@Param("postId")Long postId);
}
