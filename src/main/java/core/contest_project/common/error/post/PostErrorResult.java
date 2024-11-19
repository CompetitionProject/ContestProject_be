package core.contest_project.common.error.post;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;


@Getter
@RequiredArgsConstructor
public enum PostErrorResult {
    POST_NOT_FOUND(HttpStatus.NOT_FOUND, "post not found"),;


    private final HttpStatus status;
    private final String message;

}
