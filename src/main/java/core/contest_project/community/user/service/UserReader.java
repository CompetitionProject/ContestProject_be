package core.contest_project.community.user.service;

import core.contest_project.community.user.service.data.UserDomain;
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
}
