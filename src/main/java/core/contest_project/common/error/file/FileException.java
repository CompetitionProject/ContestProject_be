package core.contest_project.common.error.file;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class FileException extends RuntimeException{
    private final FileErrorResult fileErrorResult;

}
