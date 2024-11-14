package core.contest_project.file.repository;

import core.contest_project.file.FileLocation;
import core.contest_project.file.entity.File;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface FileJpaRepository extends JpaRepository<File,Long> {

    @Query("select file from File file" +
            " where file.post.id=:postId and file.location=:location")
    List<File> findAllByPostIdAndLocation(@Param("postId")Long postId, @Param("location") FileLocation location);

    @Modifying
    @Query("delete from File file where file.post.id=:postId and file.url in :urls")
    void deleteAllByPostId(@Param("postId")Long postId, @Param("urls")List<String> urls);

    @Modifying
    @Query("delete from File file where file.id in :ids")
    void deleteAll(@Param("ids")List<Long> ids);

    @Modifying
    @Query("delete from File file where file.post.id =:postId")
    void delete(@Param("postId")Long postId);



}
