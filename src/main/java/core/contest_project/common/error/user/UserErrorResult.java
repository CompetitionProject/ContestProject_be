package core.contest_project.common.error.user;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum UserErrorResult {
    USER_NOT_EXIST(HttpStatus.BAD_REQUEST, "존재하지 않는 유저입니다."),
    ALREADY_EXIST(HttpStatus.CONFLICT, "이미 등록된 유저가 있습니다.")
    ;


    private final HttpStatus status;
    private final String message;
}
