package core.contest_project.user.repository;

import core.contest_project.common.error.user.UserErrorResult;
import core.contest_project.common.error.user.UserException;
import core.contest_project.user.Role;
import core.contest_project.user.entity.User;
import core.contest_project.user.service.UserRepository;
import core.contest_project.user.service.data.UserDomain;
import core.contest_project.user.service.data.UserInfo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
@Slf4j
public class UserRepositoryImpl implements UserRepository {
    private final UserJpaRepository userJpaRepository;

    @Override
    public Long create(UserInfo user) {
        User newUser = User.builder()
                .nickname(user.getNickname())
                .email(user.getEmail())
                .snsProfileImageUrl(user.getSnsProfileImageUrl())
                .userField(user.getUserField())
                .duty(user.getDuty())
                .school(user.getSchool())
                .major(user.getMajor())
                .grade(user.getGrade())
                .createdAt(LocalDateTime.now())
                .rating(5.0)
                .isRatingPublic(true)
                .role(Role.ADMIN) // USER로 변경 해야함
                .teamMemberCode(UUID.randomUUID().toString())
                .popularPostNotification(false)
                .commentOnPostNotification(false)
                .replyOnCommentNotification(false)
                .build();


        return userJpaRepository.save(newUser).getId();
    }

    @Override
    public UserDomain findById(Long id) {

        return userJpaRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("user not found")).toDomain();
    }

    public UserDomain findByCode(String code){
        return userJpaRepository.findByTeamMemberCode(code)
                .orElseThrow(() -> new UserException(UserErrorResult.USER_NOT_EXIST)).toDomain();
    }

    @Override
    public UserDomain findUserProfile(Long id) {
        return null;
    }

    @Override
    public UserDomain findByNickname(String nickname) {
        User user = userJpaRepository.findByNickname(nickname)
                .orElseThrow(() -> new IllegalArgumentException("user not found"));

        return user.toDomain();
    }

    @Override
    public UserDomain findByEmail(String email) {
        return null;
    }

    @Override
    public void update(UserDomain user) {
        User findUser = userJpaRepository.findById(user.getId()).get();
        findUser.withdraw();
    }

    @Override
    public void update(UserDomain user, UserInfo userInfo) {
        User findUser = userJpaRepository.findById(user.getId()).get();
        findUser.update(userInfo);
    }

    @Override
    public List<UserDomain> findByUserIds(List<Long> userIds) {
        return userJpaRepository.findByUserIds(userIds).stream()
                .map(User::toDomain)
                .toList();
    }
}
