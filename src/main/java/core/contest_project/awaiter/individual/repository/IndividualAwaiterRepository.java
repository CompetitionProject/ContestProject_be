package core.contest_project.awaiter.individual.repository;

import core.contest_project.awaiter.individual.entity.IndividualAwaiter;
import core.contest_project.awaiter.individual.entity.IndividualAwaiterId;
import core.contest_project.contest.entity.Contest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public interface IndividualAwaiterRepository extends JpaRepository<IndividualAwaiter, IndividualAwaiterId> {

    @Query("SELECT a FROM IndividualAwaiter a " +
            "JOIN FETCH a.user " +
            "WHERE a.contest.id = :contestId " +
            "AND a.status = 'WAITING' " +
            "AND (:cursorDateTime IS NULL OR a.createdAt < :cursorDateTime) " +
            "ORDER BY a.createdAt DESC")
    List<IndividualAwaiter> findWaitingAwaiters(
            @Param("contestId") Long contestId,
            @Param("cursorDateTime") LocalDateTime cursorDateTime,
            Pageable pageable
    );

    @Query("SELECT a FROM IndividualAwaiter a " +
            "JOIN FETCH a.user " +
            "WHERE a.contest.id = :contestId AND a.status = 'WAITING'")
    Slice<IndividualAwaiter> findWaitingAwaitersByContestId(
            @Param("contestId") Long contestId,
            Pageable pageable
    );

    @Query("SELECT COUNT(a) > 0 FROM IndividualAwaiter a " +
            "WHERE a.contest.id = :contestId AND a.user.id = :userId " +
            "AND a.status = 'WAITING'")
    boolean existsWaitingAwaiterByContestAndUser(
            @Param("contestId") Long contestId,
            @Param("userId") Long userId
    );

    @Query("SELECT a.contest FROM IndividualAwaiter a " +
            "LEFT JOIN FETCH a.contest.contentImages " +
            "WHERE a.user.id = :userId " +
            "AND a.status = 'WAITING' " +
            "AND (:cursorDateTime IS NULL OR a.contest.endDate < :cursorDateTime) " +
            "ORDER BY a.contest.endDate DESC")
    List<Contest> findWaitingContests(
            @Param("userId") Long userId,
            @Param("cursorDateTime") LocalDateTime cursorDateTime,
            Pageable pageable
    );

    @Query("SELECT a.contest.id as contestId, COUNT(a) as count FROM IndividualAwaiter a " +
            "WHERE a.contest.id IN :contestIds AND a.status = 'WAITING' " +
            "GROUP BY a.contest.id")
    List<Object[]> countAwaitersGroupByContestId(@Param("contestIds") List<Long> contestIds);

    default Map<Long, Long> countAwaitersByContestIds(List<Long> contestIds) {
        List<Object[]> results = countAwaitersGroupByContestId(contestIds);
        Map<Long, Long> countMap = new HashMap<>();

        for (Object[] result : results) {
            Long contestId = ((Number) result[0]).longValue();
            Long count = ((Number) result[1]).longValue();
            countMap.put(contestId, count);
        }

        return countMap;
    }

    @Modifying
    @Query("DELETE FROM IndividualAwaiter ia WHERE ia.contest.id = :contestId")
    void deleteAllByContestId(@Param("contestId") Long contestId);
}
