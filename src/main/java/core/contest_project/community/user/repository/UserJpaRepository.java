package core.contest_project.community.user.repository;

import core.contest_project.community.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserJpaRepository extends JpaRepository<User, Long> {

    Optional<User> findByNickname(String nickname);
}
