package core.contest_project.common.error.file;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum FileErrorResult {

    FILE_NOT_FOUND(HttpStatus.NOT_FOUND, "요청한 파일을 찾을 수 없습니다."),
    FILE_UPLOAD_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "파일 업로드 중 오류가 발생했습니다."),
    FILE_DELETE_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "파일 삭제 중 오류가 발생했습니다."),
    INVALID_FILE_NAME(HttpStatus.BAD_REQUEST, "파일 이름이 유효하지 않습니다."),
    EXCEED_MAX_CONTENT_IMAGES(HttpStatus.BAD_REQUEST, "본문 이미지는 최대 4개까지만 업로드 가능합니다."),
    EXCEED_MAX_ATTACHMENTS(HttpStatus.BAD_REQUEST, "첨부파일은 최대 2개까지만 업로드 가능합니다."),
    AWS_S3_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "AWS S3 업로드 중 오류가 발생했습니다."),
    FILE_SIZE_EXCEED(HttpStatus.BAD_REQUEST, "파일 크기가 10MB를 초과할 수 없습니다."),
    EMPTY_FILE(HttpStatus.BAD_REQUEST, "파일이 비어있습니다."),
    FILE_READ_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "파일 읽기 중 오류가 발생했습니다."),
    INVALID_FILE_TYPE(HttpStatus.BAD_REQUEST, "지원하지 않는 파일 형식입니다. (지원 형식: 이미지, PDF, Word, Excel, PowerPoint)")
;
    private final HttpStatus status;
    private final String message;
}
