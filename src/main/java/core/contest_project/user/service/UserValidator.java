package core.contest_project.user.service;

import core.contest_project.common.error.user.UserErrorResult;
import core.contest_project.common.error.user.UserException;
import core.contest_project.user.Role;
import core.contest_project.user.service.data.UserDomain;
import core.contest_project.global.exception.CustomException;
import core.contest_project.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Objects;

@Component
@RequiredArgsConstructor
@Slf4j
    public class UserValidator {

    /**
     *
     * @param userId1
     * @param user2
     * @return
     *
     * 두 유저가 같은지 검증한다.
     * 수정이 필요할 듯..
     */
    public void isSame(Long  userId1, UserDomain user2) {
        if(!Objects.equals(userId1, user2.getId())){
            throw new CustomException(ErrorCode.UNAUTHORIZED_USER, "loginUser is not writer");
        }
    }

    public void isSame(Long  userId1, Long  userId2) {
        if(!Objects.equals(userId1, userId2)){
            throw new CustomException(ErrorCode.UNAUTHORIZED_USER, "loginUser is not writer");
        }
    }

    public void validateAdmin(UserDomain user) {
        if (!user.getUserInfo().getRole().equals(Role.ROLE_ADMIN)) {
            throw new UserException(UserErrorResult.UNAUTHORIZED_ADMIN_ACCESS);
        }
    }

    /*public void validateUserActive(UserDomain user) {
        if (user.isSuspended()) {
            LocalDateTime endTime = user.getSuspensionEndTime();
            String message = endTime != null ?
                    String.format("계정이 %s까지 정지되었습니다.", endTime) :
                    "계정이 영구 정지되었습니다.";
            throw new UserException(UserErrorResult.USER_SUSPENDED, message);
        }
    }*/
}
