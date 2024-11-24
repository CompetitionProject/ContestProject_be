package core.contest_project.contest.repository;

import core.contest_project.contest.dto.request.ContestCursor;
import core.contest_project.contest.entity.Contest;
import core.contest_project.contest.entity.ContestField;
import core.contest_project.contest.entity.ContestStatus;
import jakarta.persistence.LockModeType;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface ContestRepository extends JpaRepository<Contest, Long> {

    @Modifying
    @Query("UPDATE Contest c SET c.viewCount = c.viewCount + 1 WHERE c.id = :id")
    void incrementViewCount(@Param("id") Long contestId);

    /*@Query("""
    SELECT c,
        COUNT(DISTINCT a) as awaiterCount,
        COUNT(DISTINCT p) as reviewCount
    FROM Contest c
    LEFT JOIN IndividualAwaiter a ON a.contest = c AND a.status = 'WAITING'
    LEFT JOIN Post p ON p.contest = c
    LEFT JOIN c.contestFields cf
    WHERE (:fields IS NULL OR cf IN :fields)
    AND c.contestStatus IN :activeStatuses
    AND (:cursor IS NULL OR (
        CASE
            WHEN :sort = 'LATEST' THEN
                (c.createdAt < :cursorDateTime OR (c.createdAt = :cursorDateTime AND c.id < :cursorId))

            WHEN :sort = 'MOST_BOOKMARKS' THEN
                (c.bookmarkCount < :cursorBookmarkCount OR (c.bookmarkCount = :cursorBookmarkCount AND c.id < :cursorId))

            WHEN :sort = 'CLOSEST_DEADLINE' THEN
                ((c.endDate >= CURRENT_TIMESTAMP AND c.endDate > :cursorEndDate)
                    OR (c.endDate = :cursorEndDate AND c.id < :cursorId))

            WHEN :sort = 'MOST_AWAITERS' THEN
                (COUNT(DISTINCT a) < :cursorAwaiterCount
                    OR (COUNT(DISTINCT a) = :cursorAwaiterCount AND c.id < :cursorId))

            WHEN :sort = 'MOST_REVIEWS' THEN
                (COUNT(DISTINCT p) < :cursorReviewCount
                    OR (COUNT(DISTINCT p) = :cursorReviewCount AND c.id < :cursorId))
            ELSE true
        END
    ))
    GROUP BY c
    ORDER BY
    CASE
        WHEN :sort = 'LATEST' THEN c.createdAt
        WHEN :sort = 'MOST_BOOKMARKS' THEN c.bookmarkCount
        WHEN :sort = 'CLOSEST_DEADLINE' AND c.endDate >= CURRENT_TIMESTAMP THEN 1
        WHEN :sort = 'CLOSEST_DEADLINE' THEN 2
        WHEN :sort = 'MOST_AWAITERS' THEN COUNT(DISTINCT a)
        WHEN :sort = 'MOST_REVIEWS' THEN COUNT(DISTINCT p)
    END DESC,
    CASE WHEN :sort = 'CLOSEST_DEADLINE' AND c.endDate >= CURRENT_TIMESTAMP THEN c.endDate END ASC,
    c.id DESC
    """)
    List<Contest> findByContestFields(
            @Param("fields") List<ContestField> fields,
            @Param("cursor") ContestCursor cursor,
            @Param("cursorId") Long cursorId,
            @Param("cursorDateTime") LocalDateTime cursorDateTime,
            @Param("cursorBookmarkCount") Long cursorBookmarkCount,
            @Param("cursorEndDate") LocalDateTime cursorEndDate,
            @Param("cursorAwaiterCount") Long cursorAwaiterCount,
            @Param("cursorReviewCount") Long cursorReviewCount,
            @Param("sort") String sort,
            @Param("activeStatuses") List<ContestStatus> activeStatuses,
            Pageable pageable
    );*/

    // 기본 정보와 writer 조회
    @Query("SELECT c FROM Contest c " +
            "LEFT JOIN FETCH c.writer " +
            "LEFT JOIN FETCH c.contentImages " +
            "WHERE c.id = :contestId")
    Optional<Contest> findByIdWithWriter(@Param("contestId") Long contestId);

    @Query("SELECT c FROM Contest c " +
            "LEFT JOIN FETCH c.contentImages " +
            "WHERE c.id = :contestId")
    Optional<Contest> findByIdWithContentImages(@Param("contestId") Long contestId);

    @Query("SELECT c FROM Contest c " +
            "LEFT JOIN FETCH c.attachments " +
            "WHERE c.id = :contestId")
    Optional<Contest> findByIdWithAttachments(@Param("contestId") Long contestId);

    @Query("SELECT c FROM Contest c WHERE c.contestStatus != 'CLOSED'")
    List<Contest> findActiveContests();
}