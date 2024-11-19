package core.contest_project.user.repository;

import core.contest_project.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;


import java.util.List;
import java.util.Optional;

public interface UserJpaRepository extends JpaRepository<User, Long> {

    Optional<User> findByNickname(String nickname);

    @Query("SELECT u FROM User u WHERE u.id IN :userIds")
    List<User> findByUserIds(@Param("userIds") List<Long> userIds);

    @Query("SELECT u FROM User u WHERE u.teamMemberCode = :teamMemberCode")
    Optional<User> findByTeamMemberCode(@Param("teamMemberCode") String teamMemberCode);
    Optional<User> findByEmail(String email);

}
