package core.contest_project.contest.repository;

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

    @Query("SELECT c, " +
            "(SELECT COUNT(a) FROM IndividualAwaiter a WHERE a.contest = c AND a.status = 'WAITING') as awaiterCount " +
            "FROM Contest c " +
            "LEFT JOIN c.contestFields cf " +
            "WHERE (:fields is null or cf IN :fields) " +
            "AND (:lastContestId IS NULL OR c.id < :lastContestId) " +
            "AND c.contestStatus IN :activeStatuses " +
            "GROUP BY c " +
            "ORDER BY " +
            "CASE " +
            "   WHEN :sortBy = 'LATEST' THEN c.id " +
            "   WHEN :sortBy = 'MOST_BOOKMARKS' THEN c.bookmarkCount " +
            "   WHEN :sortBy = 'CLOSEST_DEADLINE' THEN " +
            "     CASE " +
            "       WHEN c.endDate < CURRENT_TIMESTAMP THEN '9999-12-31' " +
            "       ELSE c.endDate " +
            "     END " +
            "   WHEN :sortBy = 'MOST_AWAITERS' THEN " +
            "     (SELECT COUNT(a) FROM IndividualAwaiter a WHERE a.contest = c AND a.status = 'WAITING')" +
            "END DESC, " +
            "c.id DESC")
    Slice<Object[]> findByContestFields(
            @Param("fields") List<ContestField> fields,
            @Param("lastContestId") Long lastContestId,
            @Param("sortBy") String sortBy,
            @Param("activeStatuses") List<ContestStatus> activeStatuses,
            Pageable pageable
    );

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