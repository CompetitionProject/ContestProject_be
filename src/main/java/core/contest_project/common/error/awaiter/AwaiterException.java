package core.contest_project.common.error.awaiter;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class AwaiterException extends RuntimeException{

    private final AwaiterErrorResult awaiterErrorResult;
}
