package core.contest_project.global.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

//@RestControllerAdvice
@Slf4j
public class ExControllerAdvice {

    @ExceptionHandler(CustomException.class)
    public ResponseEntity<?> errorHandler(CustomException e) {
        log.error(e.getMessage());
        ErrorResponse errorResponse = new ErrorResponse(e.getErrorCode().name(), e.getMessage());
        return ResponseEntity
                .status(e.getErrorCode().getStatus())
                .body(errorResponse);
    }



}
