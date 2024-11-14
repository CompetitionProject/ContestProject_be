package core.contest_project.common.error;

import core.contest_project.common.error.contest.ContestErrorResult;
import core.contest_project.common.error.contest.ContestException;
import core.contest_project.common.error.file.FileErrorResult;
import core.contest_project.common.error.file.FileException;
import core.contest_project.common.error.user.UserErrorResult;
import core.contest_project.common.error.user.UserException;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    // 클라이언트에서 파라미터 잘못 전달한 경우
    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(
            MethodArgumentNotValidException ex, HttpHeaders headers, HttpStatusCode status, WebRequest request) {

        // exception으로 부터 에러를 가져와서 list에 담는다.
        final List<String> errorList = ex.getBindingResult()
                .getAllErrors()
                .stream()
                .map(DefaultMessageSourceResolvable::getDefaultMessage)
                .collect(Collectors.toList());

        // 해당 에러메세지 로그를 찍는다.
        log.warn("클라이언트로부터 잘못된 파라미터 전달됨 : {}", errorList);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ErrorResponse(HttpStatus.BAD_REQUEST.value(), errorList.toString()));
    }

    @ExceptionHandler({UserException.class})
    public ResponseEntity<ErrorResponse> handleUserException(final UserException exception) {
        log.warn("UserException occur:" + exception);
        UserErrorResult errorResult = exception.getUserErrorResult();
        return ResponseEntity.status(errorResult.getStatus())
                .body(new ErrorResponse(errorResult.getStatus().value(), errorResult.getMessage()));
    }

    @ExceptionHandler({ContestException.class})
    public ResponseEntity<ErrorResponse> handleContestException(final ContestException exception) {
        log.warn("UserException occur:" + exception);
        ContestErrorResult errorResult = exception.getContestErrorResult();
        return ResponseEntity.status(errorResult.getStatus())
                .body(new ErrorResponse(errorResult.getStatus().value(), errorResult.getMessage()));
    }

    @ExceptionHandler({FileException.class})
    public ResponseEntity<ErrorResponse> handleFileException(final FileException exception) {
        log.warn("FileException occur: " + exception);
        FileErrorResult errorResult = exception.getFileErrorResult();
        return ResponseEntity.status(errorResult.getStatus())
                .body(new ErrorResponse(errorResult.getStatus().value(), errorResult.getMessage()));
    }

    @RequiredArgsConstructor
    @Getter
    static class ErrorResponse {
        private final int code;
        private final String message;
    }
}
