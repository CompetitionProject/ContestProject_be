package core.contest_project.global.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {

    NOT_FOUND(HttpStatus.NOT_FOUND, "not found"),
    UNAUTHORIZED_USER(HttpStatus.UNAUTHORIZED, "Unauthorized user"),;

    private final HttpStatus status;
    private final String message;
}
