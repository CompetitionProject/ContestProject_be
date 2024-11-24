package core.contest_project.user.service;

import core.contest_project.moderation.SuspensionStatus;
import core.contest_project.user.entity.User;
import core.contest_project.user.service.data.UserDomain;
import core.contest_project.user.service.data.UserInfo;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface UserRepository {
    Long create(UserInfo user);

     UserDomain findById(Long id) ;
     UserDomain findUserProfile(Long id);
     UserDomain findByNickname(String nickname);
     UserDomain findByEmail(String email);


    UserDomain findByCode(String code);
    void update(UserDomain user);
    void update(UserDomain user, UserInfo userInfo);

    List<UserDomain> findByUserIds(List<Long> userIds);

    Long countTodaySignUps();

    void updateSuspensionStatus(Long userId, SuspensionStatus status, LocalDateTime endTime, int warningCount);

}
