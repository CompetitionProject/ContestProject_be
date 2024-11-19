package core.contest_project.common.error.post;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class PostException extends RuntimeException{
    private final PostErrorResult errorResult;
}
