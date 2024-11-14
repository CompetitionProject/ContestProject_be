package core.contest_project.common.error.contest;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ContestErrorResult {

    CONTEST_NOT_EXIST(HttpStatus.BAD_REQUEST, "존재하지 않는 공모전입니다."),
    INVALID_VALUE(HttpStatus.BAD_REQUEST, "필수값이 누락되거나 부적절한 값이 반환되었습니다."),
    UNAUTHORIZED_DELETION(HttpStatus.FORBIDDEN, "공모전을 삭제할 권한이 없습니다."),
    IMAGE_NOT_FOUND(HttpStatus.NOT_FOUND, "요청한 이미지를 찾을 수 없습니다."),
    FILE_UPLOAD_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "파일 업로드 중 오류가 발생했습니다."),
    FILE_DELETE_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "파일 삭제 중 오류가 발생했습니다."),
    INVALID_FILE_TYPE(HttpStatus.BAD_REQUEST, "지원하지 않는 파일 형식입니다."),
    FILE_SIZE_EXCEED(HttpStatus.BAD_REQUEST, "파일 크기가 제한을 초과했습니다."),
    INVALID_CATEGORY(HttpStatus.BAD_REQUEST, "유효하지 않은 카테고리입니다."),
    UNAUTHORIZED_UPDATE(HttpStatus.FORBIDDEN, "관리자만 수정할 수 있습니다."),
    UNAUTHORIZED_DELETE(HttpStatus.FORBIDDEN, "관리자만 삭제할 수 있습니다."),
    INVALID_SEARCH_KEYWORD(HttpStatus.BAD_REQUEST, "검색어를 입력해주세요."),
    UNAUTHORIZED_ACTION(HttpStatus.FORBIDDEN, "이 작업을 수행할 권한이 없습니다."),
    POSTER_REQUIRED(HttpStatus.BAD_REQUEST, "포스터 이미지는 필수입니다."),
    ALREADY_WAITING(HttpStatus.BAD_REQUEST, "이미 대기 중인 공모전입니다."),
    APPLICATION_NOT_FOUND(HttpStatus.NOT_FOUND, "대기 신청을 찾을 수 없습니다.");

    private final HttpStatus status;
    private final String message;
}
