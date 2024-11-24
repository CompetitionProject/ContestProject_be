package core.contest_project.user.service;

import core.contest_project.user.service.data.UserDomain;
import core.contest_project.user_detail.service.UserDetailInfo;
import core.contest_project.user_detail.service.UserDetailRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;


@Component
@RequiredArgsConstructor
@Slf4j
public class UserReader {
    private final UserRepository userRepository;
    private final UserDetailRepository userDetailRepository;

    public UserDomain getUser(Long userId) {
        UserDomain user = userRepository.findById(userId);
        UserDetailInfo userDetails = userDetailRepository.findAllByUser(user.getId());
        user.setUserDetail(userDetails);
        return user;
    }

    public UserDomain getUserByEmail(String email){
        return userRepository.findByEmail(email);
    }

    public UserDomain getUserByCode(String code) {
        return userRepository.findByCode(code);
    }
    public List<UserDomain> getUsersByIds(List<Long> userIds) {
        if (userIds.isEmpty()) {
            return Collections.emptyList();
        }

        return userRepository.findByUserIds(userIds);
    }

    public Long getTodaySignUpCount() {
        return userRepository.countTodaySignUps();
    }
}
