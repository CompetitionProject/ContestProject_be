package core.contest_project.community.post.repository;

import core.contest_project.community.post.entity.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.Optional;

public interface PostJpaRepository extends JpaRepository<Post, Long> {


    @Query("select p from Post p join fetch p.writer where p.id=:postId")
    Optional<Post> findByPostIdJoinWriter(@Param("postId")Long postId);

    @Query("select p from Post p" +
            " join fetch p.writer" +
            " left join fetch p.files" +
            " where p.id=:postId")
    Optional<Post> findByPostIdJoinWriterAndFiles(@Param("postId")Long postId);


    @Query("select post from Post post" +
            " join fetch post.writer" +
            " left join fetch post.comments")
    Slice<Post> findSliceBy(Pageable pageable);

    @Query("select post from Post post" +
            " join fetch post.writer" +
            " left join fetch post.comments")
    Page<Post> findPageBy(Pageable pageable);

    @Query("select p from Post p " +
            "join Scrap s on s.post.id = p.id " +
            "join fetch p.writer " +
            "where s.user.id = :userId ")
    Slice<Post> findScrapedPostsByUserId(@Param("userId")Long userId, Pageable pageable);


    @Query("select p from Post p" +
            " join fetch p.writer writer" +
            " where writer.id=:userId")
    Slice<Post> findPostsByUserId(@Param("userId") Long userId, Pageable pageable);

    @Query("select p from Post p" +
            " join fetch p.writer" +
            " left join fetch p.comments" +
            " where p.createAt > :oneWeekAgo")
    Slice<Post> findPopularPosts(@Param("oneWeekAgo")LocalDateTime oneWeekAgo, Pageable pageable);

    @Query("select p from Post p" +
            " join fetch p.writer" +
            " left join fetch p.comments" +
            " where p.createAt > :oneWeekAgo and p.contest.id=:contestId")
    Slice<Post> findPopularTips(@Param("oneWeekAgo")LocalDateTime oneWeekAgo, Pageable pageable, @Param("contestId")Long contestId);

    @Query("select p from Post p" +
            " join fetch p.writer" +
            " left join fetch p.comments" +
            " where p.contest.id=:contestId")
    Slice<Post> findRecentTips(Pageable pageable, @Param("contestId")Long contestId);



    @Query("select p from Post p" +
            " where p.writer.teamMemberCode=:teamMemberCode")
    Slice<Post>findPostsByTeamMemberCode(@Param("teamMemberCode")String teamMemberCode,Pageable pageable);

    @Modifying
    @Query("delete from Post p where p.id=:postId")
    void deletePostById(@Param("postId")Long postId);

    @Modifying
    @Query("update Post p set p.viewCount =p.viewCount+1 where p.id=:postId ")
    void updateViewCount(@Param("postId")Long postId);

    @Modifying
    @Query("update Post p set p.likeCount=p.likeCount+1 where p.id=:postId")
    void increaseLikeCount(@Param("postId")Long postId);

    @Modifying
    @Query("update Post p set p.likeCount=p.likeCount-1 where p.id=:postId and p.likeCount>0")
    void decreaseLikeCount(@Param("postId")Long postId);

    @Modifying
    @Query("update Post p set p.nextAnonymousSeq =p.nextAnonymousSeq+1 where p.id=:postId ")
    void updateNextAnonymousSeq(@Param("postId")Long postId);

}
