package core.contest_project.user.service;

import core.contest_project.user.service.data.UserDomain;
import core.contest_project.user.service.data.UserInfo;

public interface UserRepository {
    Long create(UserInfo user);

     UserDomain findById(Long id) ;
     UserDomain findUserProfile(Long id);
     UserDomain findByNickname(String nickname);
     UserDomain findByEmail(String email);


    void update(UserDomain user);
    void update(UserDomain user, UserInfo userInfo);
}
