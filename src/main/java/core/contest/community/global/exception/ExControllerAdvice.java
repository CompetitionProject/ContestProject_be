package core.contest.community.global.exception;

import core.contest.community.global.exception.CustomException;
import core.contest.community.global.exception.ErrorResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@Slf4j
public class ExControllerAdvice {

    @ExceptionHandler(core.contest.community.global.exception.CustomException.class)
    public ResponseEntity<?> errorHandler(CustomException e) {
        log.error(e.getMessage());
        core.contest.community.global.exception.ErrorResponse errorResponse = new ErrorResponse(e.getErrorCode().name(), e.getMessage());
        return ResponseEntity
                .status(e.getErrorCode().getStatus())
                .body(errorResponse);
    }

}
