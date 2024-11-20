package core.contest_project.common.error.team;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class TeamException extends RuntimeException{
    private final TeamErrorResult teamErrorResult;
}
