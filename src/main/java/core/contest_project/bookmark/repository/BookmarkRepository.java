package core.contest_project.bookmark.repository;

import core.contest_project.bookmark.entity.Bookmark;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface BookmarkRepository extends JpaRepository<Bookmark, Long> {

    @Query("SELECT COUNT(b) > 0 FROM Bookmark b WHERE b.contest.id = :contestId AND b.user.id = :userId")
    boolean existsByContestIdAndUserId(@Param("contestId") Long contestId, @Param("userId") Long userId);

    @Modifying
    @Query("DELETE FROM Bookmark b WHERE b.contest.id = :contestId AND b.user.id = :userId")
    void deleteByContestIdAndUserId(@Param("contestId") Long contestId, @Param("userId") Long userId);

    @Query("SELECT b.id FROM Bookmark b WHERE b.contest.id IN :contestIds AND b.user.id = :userId")
    List<Long> findBookmarkedContestIds(@Param("contestIds") List<Long> contestIds, @Param("userId") Long userId);
}
