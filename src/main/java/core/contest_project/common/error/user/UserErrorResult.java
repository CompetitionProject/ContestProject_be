package core.contest_project.common.error.user;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum UserErrorResult {
    USER_NOT_EXIST(HttpStatus.BAD_REQUEST, "존재하지 않는 유저입니다."),
    ALREADY_EXIST(HttpStatus.CONFLICT, "이미 등록된 유저가 있습니다."),
    INVALID_MEMBER_CODE(HttpStatus.NOT_FOUND, "유효하지 않은 멤버 코드입니다."),
    UNAUTHORIZED_ADMIN_ACCESS(HttpStatus.FORBIDDEN, "관리자 권한이 필요한 기능입니다."),
    USER_SUSPENDED(HttpStatus.FORBIDDEN, "사용자가 정지된 상태입니다.")
    ;


    private final HttpStatus status;
    private final String message;
}
