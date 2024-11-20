package core.contest_project.common.error.awaiter;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum AwaiterErrorResult {

    // 존재 여부 관련
    AWAITER_NOT_FOUND(HttpStatus.NOT_FOUND, "대기자를 찾을 수 없습니다."),

    // 상태 관련
    INVALID_AWAITER_STATUS(HttpStatus.BAD_REQUEST, "유효하지 않은 대기 상태입니다."),
    ALREADY_MATCHED(HttpStatus.BAD_REQUEST, "이미 매칭이 완료된 대기입니다."),
    ALREADY_CANCELED(HttpStatus.BAD_REQUEST, "이미 취소된 대기입니다."),

    // 중복 관련
    ALREADY_WAITING(HttpStatus.CONFLICT, "이미 대기 중인 상태입니다."),
    DUPLICATE_INDIVIDUAL_AWAITER(HttpStatus.CONFLICT, "해당 공모전에 이미 개인 대기자로 등록되어 있습니다."),
    DUPLICATE_TEAM_AWAITER(HttpStatus.CONFLICT, "해당 공모전에 이미 팀 대기자로 등록되어 있습니다."),

    // 유효성 검증 관련
    INVALID_AWAITER_TYPE(HttpStatus.BAD_REQUEST, "유효하지 않은 대기자 유형입니다."),
    MISSING_USER_INFO(HttpStatus.BAD_REQUEST, "개인 대기자의 사용자 정보가 누락되었습니다."),
    MISSING_TEAM_INFO(HttpStatus.BAD_REQUEST, "팀 대기자의 팀 정보가 누락되었습니다."),
    INVALID_USER_TEAM_COMBINATION(HttpStatus.BAD_REQUEST, "대기자 유형에 맞지 않는 정보가 포함되어 있습니다."),

    // 권한 관련
    UNAUTHORIZED_ACTION(HttpStatus.FORBIDDEN, "해당 작업을 수행할 권한이 없습니다."),
    NOT_AWAITER_OWNER(HttpStatus.FORBIDDEN, "대기자의 소유자가 아닙니다."),

    // 제한 관련
    MAX_AWAITER_REACHED(HttpStatus.BAD_REQUEST, "해당 공모전의 최대 대기자 수에 도달했습니다.");


    private final HttpStatus status;
    private final String message;
}
