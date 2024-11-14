package core.contest_project.user.service;

import core.contest_project.user.entity.User;
import core.contest_project.user.service.data.UserDomain;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class UserReader {
    private final UserRepository userRepository;

    public UserDomain getUser(Long userId) {
        return userRepository.findById(userId);
    }

    public UserDomain getUserByEmail(String email){
        return userRepository.findByEmail(email);
    }
}
