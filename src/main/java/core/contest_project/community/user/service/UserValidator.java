package core.contest_project.community.user.service;

import core.contest_project.community.user.service.data.UserDomain;
import core.contest_project.community.global.exception.CustomException;
import core.contest_project.community.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

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

}
