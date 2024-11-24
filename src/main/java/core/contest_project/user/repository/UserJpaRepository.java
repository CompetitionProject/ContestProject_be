package core.contest_project.user.repository;

import core.contest_project.moderation.SuspensionStatus;
import core.contest_project.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;


import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface UserJpaRepository extends JpaRepository<User, Long> {

    Optional<User> findByNickname(String nickname);

    @Query("SELECT u FROM User u WHERE u.id IN :userIds")
    List<User> findByUserIds(@Param("userIds") List<Long> userIds);

    @Query("SELECT u FROM User u WHERE u.teamMemberCode = :teamMemberCode")
    Optional<User> findByTeamMemberCode(@Param("teamMemberCode") String teamMemberCode);
    Optional<User> findByEmail(String email);

    @Query("SELECT COUNT(u) FROM User u " +
            "WHERE DATE(u.createdAt) = CURRENT_DATE " +
            "AND u.isDeleted = false")
    Long countTodaySignUps();

    @Modifying
    @Query("UPDATE User u SET " +
            "u.suspensionStatus = :status, " +
            "u.suspensionEndTime = :endTime, " +
            "u.warningCount = :warningCount " +
            "WHERE u.id = :userId")
    void updateSuspensionStatus(
            @Param("userId") Long userId,
            @Param("status") SuspensionStatus status,
            @Param("endTime") LocalDateTime endTime,
            @Param("warningCount") int warningCount
    );
}
