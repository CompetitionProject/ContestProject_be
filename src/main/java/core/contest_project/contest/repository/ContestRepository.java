package core.contest_project.contest.repository;

import core.contest_project.contest.entity.Contest;
import core.contest_project.contest.entity.ContestField;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ContestRepository extends JpaRepository<Contest, Long> {

    @Modifying
    @Query("UPDATE Contest c SET c.viewCount = c.viewCount + 1 WHERE c.id = :id")
    void incrementViewCount(@Param("id") Long contestId);

    @Query("SELECT DISTINCT c FROM Contest c" +
            " LEFT JOIN c.contestFields cf" +
            " WHERE (:fields is null or cf IN :fields)" +
            " AND (:lastContestId IS NULL OR c.id < :lastContestId)" +
            " ORDER BY " +
            " CASE " +
            "   WHEN :sortBy = 'LATEST' THEN c.id " +
            "   WHEN :sortBy = 'MOST_BOOKMARKS' THEN c.bookmarkCount " +
            "   WHEN :sortBy = 'CLOSEST_DEADLINE' THEN " +
            "     CASE " +
            "       WHEN c.endDate < CURRENT_TIMESTAMP THEN '9999-12-31' " +
            "       ELSE c.endDate " +
            "     END " +
            " END DESC")
    Slice<Contest> findByContestFields(
            @Param("fields") List<ContestField> fields,
            @Param("lastContestId") Long lastContestId,
            @Param("sortBy") String sortBy,
            Pageable pageable
    );

    /*@Query("SELECT DISTINCT c FROM Contest c" +
            " LEFT JOIN c.contestFields cf" +
            " WHERE (:fields is null or cf IN :fields)" +  // IS EMPTY 대신 is null 사용
            " AND (:lastContestId IS NULL OR c.id < :lastContestId)" +
            " ORDER BY c.id DESC")
    Slice<Contest> findByContestFields(
            @Param("fields") List<ContestField> fields,
            @Param("lastContestId") Long lastContestId,
            Pageable pageable
    );*/

//    @Query("SELECT NEW core.contest.contest.dto.response.ContestSimpleResponse(" +
//            "c.id, c.title, c.bookmarkCount, " +
//            "CASE WHEN c.endDate < CURRENT_TIMESTAMP THEN -1 " +
//            "ELSE DATEDIFF(c.endDate, CURRENT_TIMESTAMP) END, " +
//            "c.endDate, " +
//            "CASE WHEN pi IS NULL THEN CAST(NULL AS string) ELSE pi.storeName END, " +
//            "cf, " +
//            "c.writer.id) " +
//            "FROM Contest c " +
//            "LEFT JOIN c.posterImage pi " +
//            "LEFT JOIN c.contestFields cf " +
//            "WHERE (SIZE(:fields) = 0 OR cf IN :fields) " +
//            "AND (:lastContestId IS NULL OR c.id < :lastContestId) " +
//            "ORDER BY c.id DESC")
//    Slice<ContestSimpleResponse> findContestsByField(
//            @Param("fields") List<ContestField> fields,
//            @Param("lastContestId") Long lastContestId,
//            Pageable pageable
//    );

    // 기본 정보와 writer 조회
    @Query("SELECT c FROM Contest c " +
            "LEFT JOIN FETCH c.writer " +
            "LEFT JOIN FETCH c.posterImage " +
            "WHERE c.id = :contestId")
    Optional<Contest> findByIdWithWriter(@Param("contestId") Long contestId);

    // 기본 정보와 content만 조회
    @Query("SELECT c FROM Contest c WHERE c.id = :contestId")
    Optional<Contest> findByIdWithContent(@Param("contestId") Long contestId);

    // 이미지만 조회
    @Query("SELECT c FROM Contest c LEFT JOIN FETCH c.contentImages WHERE c.id = :contestId")
    Contest findByIdWithContentImages(@Param("contestId") Long contestId);

    // 첨부파일만 조회
    @Query("SELECT c FROM Contest c LEFT JOIN FETCH c.attachments WHERE c.id = :contestId")
    Contest findByIdWithAttachments(@Param("contestId") Long contestId);

    // 포스터 이미지 조회
    @Query("SELECT c FROM Contest c " +
            "LEFT JOIN FETCH c.posterImage " +
            "WHERE c.id = :contestId")
    Contest findByIdWithPosterImage(@Param("contestId") Long contestId);
}