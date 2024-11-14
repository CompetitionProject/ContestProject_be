package core.contest_project.common.error.contest;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class ContestException extends RuntimeException{
    private final ContestErrorResult contestErrorResult;
}
