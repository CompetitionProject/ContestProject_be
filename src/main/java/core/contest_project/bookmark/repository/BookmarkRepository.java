package core.contest_project.bookmark.repository;

import core.contest_project.bookmark.entity.Bookmark;
import core.contest_project.contest.entity.Contest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface BookmarkRepository extends JpaRepository<Bookmark, Long> {

    @Query("SELECT COUNT(b) > 0 FROM Bookmark b WHERE b.contest.id = :contestId AND b.user.id = :userId")
    boolean existsByContestIdAndUserId(@Param("contestId") Long contestId, @Param("userId") Long userId);

    @Modifying
    @Query("DELETE FROM Bookmark b WHERE b.contest.id = :contestId AND b.user.id = :userId")
    void deleteByContestIdAndUserId(@Param("contestId") Long contestId, @Param("userId") Long userId);

    @Query("SELECT b.id FROM Bookmark b WHERE b.contest.id IN :contestIds AND b.user.id = :userId")
    List<Long> findBookmarkedContestIds(@Param("contestIds") List<Long> contestIds, @Param("userId") Long userId);

    @Modifying
    @Query("delete from Bookmark b where b.contest.id=:contestId")
    void deleteAllByContestId(@Param("contestId") Long contestId);

    @Query("SELECT b.contest FROM Bookmark b " +
            "LEFT JOIN FETCH b.contest.contentImages " +
            "WHERE b.user.id = :userId " +
            "AND (:cursorDateTime IS NULL OR b.contest.endDate < :cursorDateTime) " +
            "ORDER BY b.contest.endDate DESC")
    List<Contest> findBookmarkedContests(
            @Param("userId") Long userId,
            @Param("cursorDateTime") LocalDateTime cursorDateTime,
            Pageable pageable
    );
}
