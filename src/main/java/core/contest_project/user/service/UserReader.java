package core.contest_project.user.service;

import core.contest_project.user.entity.User;
import core.contest_project.user.repository.UserJpaRepository;
import core.contest_project.user.service.data.UserDomain;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;


@Component
@RequiredArgsConstructor
@Slf4j
public class UserReader {
    private final UserRepository userRepository;
    private final UserJpaRepository userJpaRepository;

    public UserDomain getUser(Long userId) {
        return userRepository.findById(userId);
    }

    public UserDomain getUserByCode(String code) {
        return userRepository.findByCode(code);
    }
    public List<UserDomain> getUsersByIds(List<Long> userIds) {
        if (userIds.isEmpty()) {
            return Collections.emptyList();
        }

        return userJpaRepository.findByUserIds(userIds).stream()
                .map(User::toDomain)
                .collect(Collectors.toList());
    }
}
