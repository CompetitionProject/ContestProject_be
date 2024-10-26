package core.contest.community.user.service;

import core.contest.community.user.service.data.UserDomain;
import core.contest.community.user.service.data.UserInfo;

public interface UserRepository {
    Long create(UserInfo user);

     UserDomain findById(Long id) ;
     UserDomain findByNickname(String nickname);

    void update(UserDomain user);
    void update(UserDomain user, UserInfo userInfo);
}
