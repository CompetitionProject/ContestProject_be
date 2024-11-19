package core.contest_project.team.service;

import core.contest_project.common.error.team.TeamErrorResult;
import core.contest_project.common.error.team.TeamException;
import core.contest_project.team.entity.Team;
import core.contest_project.team.entity.join.RequestStatus;
import core.contest_project.team.entity.member.TeamMemberId;
import core.contest_project.team.repository.TeamMemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
@RequiredArgsConstructor
public class TeamValidator {
    private final TeamMemberRepository teamMemberRepository;

    // 팀장 권한 검증
    public void validateLeader(Team team, Long userId) {
        if (!Objects.equals(team.getLeader().getId(), userId)) {
            throw new TeamException(TeamErrorResult.UNAUTHORIZED_ACTION);
        }
    }


    // 이미 팀원인지 검증
    public void validateNotTeamMember(Long teamId, Long userId) {
        TeamMemberId memberId = new TeamMemberId(teamId, userId);
        if (teamMemberRepository.existsById(memberId)) {
            throw new TeamException(TeamErrorResult.ALREADY_TEAM_MEMBER);
        }
    }

    // 팀원인지 검증 (강퇴/탈퇴 시)
    public void validateTeamMember(Team team, Long userId) {
        if (!team.isMember(userId)) {
            throw new TeamException(TeamErrorResult.MEMBER_NOT_FOUND);
        }
    }

    // 팀장이 아닌지 검증 (강퇴 시)
    public void validateNotLeader(Team team, Long userId) {
        if (Objects.equals(team.getLeader().getId(), userId)) {
            throw new TeamException(TeamErrorResult.CANNOT_EXPEL_LEADER);
        }
    }

    // 팀장인지 검증 (탈퇴 시)
    public void validateNotTeamLeader(Team team, Long userId) {
        if (Objects.equals(team.getLeader().getId(), userId)) {
            throw new TeamException(TeamErrorResult.LEADER_CANNOT_LEAVE);
        }
    }

    // 요청 상태 검증
    public void validateRequestPending(RequestStatus requestStatus) {
        if (requestStatus != RequestStatus.PENDING) {
            throw new TeamException(TeamErrorResult.INVALID_REQUEST_STATUS);
        }
    }
    /*// 팀 조인 요청 중복 여부 검증
    public void validateRequestNotExists(Long teamId, Long userId) {
        TeamJoinRequestId requestId = new TeamJoinRequestId(teamId, userId);
        if (teamJoinRequestRepository.existsById(requestId)) {
            throw new TeamException(TeamErrorResult.ALREADY_REQUESTED);
        }
    }*/


    // 최대 멤버 수 검증
    public void validateMaxMemberCount(int maxMemberCount, Team team) {
        if (maxMemberCount < team.getMembers().size()) {
            throw new TeamException(TeamErrorResult.INVALID_MAX_MEMBER_COUNT);
        }
    }
}