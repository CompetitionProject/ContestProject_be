package core.contest_project.common.error.comment;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class CommentException extends RuntimeException{
    private final CommentErrorResult errorResult;
}
