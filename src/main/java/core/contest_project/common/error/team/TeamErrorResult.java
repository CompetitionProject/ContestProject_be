package core.contest_project.common.error.team;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor

public enum TeamErrorResult {

    TEAM_NOT_FOUND(HttpStatus.NOT_FOUND, "존재하지 않는 팀입니다."),
    UNAUTHORIZED_ACTION(HttpStatus.FORBIDDEN, "팀장 권한이 필요합니다."),
    INVALID_MAX_MEMBER_COUNT(HttpStatus.BAD_REQUEST, "현재 멤버 수보다 작은 값으로 수정할 수 없습니다."),
    TEAM_NOT_RECRUITING(HttpStatus.BAD_REQUEST, "현재 팀원을 모집하지 않는 팀입니다."),
    ALREADY_TEAM_MEMBER(HttpStatus.BAD_REQUEST, "이미 팀의 멤버입니다."),
    ALREADY_REQUESTED(HttpStatus.BAD_REQUEST, "이미 가입 신청을 한 팀입니다."),
    JOIN_REQUEST_NOT_FOUND(HttpStatus.NOT_FOUND, "가입 신청을 찾을 수 없습니다."),
    INVALID_REQUEST_STATUS(HttpStatus.BAD_REQUEST, "처리할 수 없는 상태의 가입 신청입니다."),
    TEAM_IS_FULL(HttpStatus.BAD_REQUEST, "최대 멤버 수를 초과할 수 없습니다."),
    MEMBER_NOT_FOUND(HttpStatus.NOT_FOUND, "팀원을 찾을 수 없습니다."),
    CANNOT_EXPEL_LEADER(HttpStatus.BAD_REQUEST, "팀장은 강퇴할 수 없습니다."),
    NEW_LEADER_REQUIRED(HttpStatus.BAD_REQUEST, "새로운 팀장 정보가 필요합니다."),
    ALREADY_LEADER(HttpStatus.BAD_REQUEST, "이미 팀장입니다."),
    LEADER_CANNOT_LEAVE(HttpStatus.BAD_REQUEST, "팀장은 탈퇴할 수 없습니다. 먼저 팀장을 위임해주세요."),
    NOT_LEADER(HttpStatus.BAD_REQUEST, "팀장이 아닙니다."),
    ALREADY_INVITED(HttpStatus.CONFLICT, "이미 초대한 대기자입니다."),
    INVITATION_NOT_FOUND(HttpStatus.NOT_FOUND, "초대 정보를 찾을 수 없습니다."),
    INVALID_INVITATION_STATUS(HttpStatus.BAD_REQUEST, "유효하지 않은 초대 상태입니다.");

    private final HttpStatus status;
    private final String message;
}
