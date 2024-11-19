package core.contest_project.common.error.comment;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;


@Getter
@RequiredArgsConstructor
public enum CommentErrorResult {
    COMMENT_NOT_FOUND(HttpStatus.NOT_FOUND, "comment not found"),;


    private final HttpStatus status;
    private final String message;

}
