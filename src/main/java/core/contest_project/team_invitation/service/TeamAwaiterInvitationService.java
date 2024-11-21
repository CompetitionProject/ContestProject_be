package core.contest_project.team_invitation.service;

import core.contest_project.awaiter.individual.entity.IndividualAwaiter;
import core.contest_project.awaiter.individual.entity.IndividualAwaiterId;
import core.contest_project.awaiter.individual.repository.IndividualAwaiterRepository;
import core.contest_project.common.error.awaiter.AwaiterErrorResult;
import core.contest_project.common.error.awaiter.AwaiterException;
import core.contest_project.common.error.contest.ContestErrorResult;
import core.contest_project.common.error.contest.ContestException;
import core.contest_project.common.error.team.TeamErrorResult;
import core.contest_project.common.error.team.TeamException;
import core.contest_project.contest.entity.Contest;
import core.contest_project.contest.repository.ContestRepository;
import core.contest_project.team.entity.Team;
import core.contest_project.team.entity.member.TeamMember;
import core.contest_project.team.entity.member.TeamMemberId;
import core.contest_project.team.entity.member.TeamMemberRole;
import core.contest_project.team.repository.TeamMemberRepository;
import core.contest_project.team.repository.TeamRepository;
import core.contest_project.team.service.TeamValidator;
import core.contest_project.team_invitation.dto.response.TeamInvitationResponse;
import core.contest_project.team_invitation.dto.response.TeamSentInvitationResponse;
import core.contest_project.team_invitation.entity.TeamAwaiterInvitation;
import core.contest_project.team_invitation.entity.TeamAwaiterInvitationId;
import core.contest_project.team_invitation.repository.TeamAwaiterInvitationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class TeamAwaiterInvitationService {

    private final TeamRepository teamRepository;
    private final TeamAwaiterInvitationRepository teamAwaiterInvitationRepository;
    private final IndividualAwaiterRepository individualAwaiterRepository;
    private final TeamMemberRepository teamMemberRepository;
    private final TeamValidator teamValidator;
    private final ContestRepository contestRepository;

    private static final int PAGE_SIZE = 20;


    public void inviteAwaiter(Long teamId, Long contestId, Long targetId, Long userId) {
        // 팀 조회 및 검증
        Team team = teamRepository.findById(teamId)
                .orElseThrow(() -> new TeamException(TeamErrorResult.TEAM_NOT_FOUND));

        // 공모전 조회
        Contest contest = contestRepository.findById(contestId)
                .orElseThrow(() -> new ContestException(ContestErrorResult.CONTEST_NOT_EXIST));

        // 검증
        teamValidator.validateLeader(team, userId);

        // 대기자 조회
        IndividualAwaiter awaiter = individualAwaiterRepository
                .findById(new IndividualAwaiterId(contestId, targetId))
                .orElseThrow(() -> new AwaiterException(AwaiterErrorResult.AWAITER_NOT_FOUND));

        if (!awaiter.isWaiting()) {
            throw new AwaiterException(AwaiterErrorResult.INVALID_AWAITER_STATUS);
        }

        // PENDING 상태의 초대만 체크
        TeamAwaiterInvitationId invitationId = new TeamAwaiterInvitationId(teamId, targetId);
        if (teamAwaiterInvitationRepository.existsPendingById(invitationId)) {
            throw new TeamException(TeamErrorResult.ALREADY_INVITED);
        }

        // 초대장 생성
        TeamAwaiterInvitation invitation = TeamAwaiterInvitation.createInvitation(
                team,
                awaiter.getUser(),
                contestId
        );
        teamAwaiterInvitationRepository.save(invitation);
    }

    @Transactional
    public void acceptInvitation(Long userId) {
        // 사용자에게 온 PENDING 상태의 초대 찾기
        TeamAwaiterInvitation invitation = teamAwaiterInvitationRepository.findPendingByTargetUserId(userId)
                .orElseThrow(() -> new TeamException(TeamErrorResult.INVITATION_NOT_FOUND));

        Team team = invitation.getTeam();

        // 팀원 추가
        TeamMember newMember = TeamMember.createTeamMember(
                team,
                invitation.getTargetUser(),
                TeamMemberRole.MEMBER
        );
        teamMemberRepository.save(newMember);

        // 대기자 목록에서 삭제
        IndividualAwaiterId awaiterId = new IndividualAwaiterId(invitation.getContestId(), userId);
        individualAwaiterRepository.deleteById(awaiterId);

        // 초대 상태 업데이트
        TeamAwaiterInvitation acceptedInvitation = invitation.accept();
        teamAwaiterInvitationRepository.save(acceptedInvitation);

        teamRepository.save(team);
    }

    @Transactional
    public void rejectInvitation(Long userId) {
        // 나에게 온 PENDING 상태의 초대 찾기
        TeamAwaiterInvitation invitation = teamAwaiterInvitationRepository.findPendingByTargetUserId(userId)
                .orElseThrow(() -> new TeamException(TeamErrorResult.INVITATION_NOT_FOUND));

        // 2. 초대 거절 상태로 변경
        TeamAwaiterInvitation rejectedInvitation = invitation.reject();
        teamAwaiterInvitationRepository.save(rejectedInvitation);
    }

    @Transactional(readOnly = true)
    public Slice<TeamInvitationResponse> getMyInvitations(Long userId, Long contestId, LocalDateTime cursorDateTime) {
        Pageable pageable = PageRequest.of(0, PAGE_SIZE + 1);

        List<TeamAwaiterInvitation> invitations = teamAwaiterInvitationRepository
                .findMyInvitations(userId, cursorDateTime, pageable);

        boolean hasNext = invitations.size() > PAGE_SIZE;
        List<TeamAwaiterInvitation> content = hasNext ?
                invitations.subList(0, PAGE_SIZE) : invitations;

        // contestId로 contest 조회
        Contest contest = contestRepository.findById(contestId)
                .orElseThrow(() -> new ContestException(ContestErrorResult.CONTEST_NOT_EXIST));

        List<TeamInvitationResponse> responses = content.stream()
                .map(invitation -> TeamInvitationResponse.from(invitation, contest.getTitle()))
                .collect(Collectors.toList());

        return new SliceImpl<>(responses, pageable, hasNext);
    }

    private TeamAwaiterInvitation findAndValidatePendingInvitation(Long teamId, Long targetId, Long userId) {
        // 1. PENDING 상태의 초대장만 조회
        TeamAwaiterInvitationId invitationId = new TeamAwaiterInvitationId(teamId, targetId);
        TeamAwaiterInvitation invitation = teamAwaiterInvitationRepository.findPendingById(invitationId)
                .orElseThrow(() -> new TeamException(TeamErrorResult.INVITATION_NOT_FOUND));

        // 2. 권한 체크
        if (!Objects.equals(invitation.getTargetUser().getId(), userId)) {
            throw new TeamException(TeamErrorResult.UNAUTHORIZED_ACTION);
        }

        return invitation;
    }

    @Transactional(readOnly = true)
    public Slice<TeamSentInvitationResponse> getTeamSentInvitations(
            Long teamId,
            Long userId,
            LocalDateTime cursorDateTime
    ) {
        // 팀 조회
        Team team = teamRepository.findById(teamId)
                .orElseThrow(() -> new TeamException(TeamErrorResult.TEAM_NOT_FOUND));

        // 팀 멤버인지 확인 (팀원이면 조회 가능)
        TeamMemberId memberId = new TeamMemberId(teamId, userId);
        if (!teamMemberRepository.existsById(memberId)) {
            throw new TeamException(TeamErrorResult.UNAUTHORIZED_ACTION);
        }

        Pageable pageable = PageRequest.of(0, PAGE_SIZE + 1);

        List<TeamAwaiterInvitation> invitations = teamAwaiterInvitationRepository
                .findTeamSentInvitations(teamId, cursorDateTime, pageable);

        boolean hasNext = invitations.size() > PAGE_SIZE;
        List<TeamAwaiterInvitation> content = hasNext ?
                invitations.subList(0, PAGE_SIZE) : invitations;

        List<TeamSentInvitationResponse> responses = content.stream()
                .map(TeamSentInvitationResponse::from)
                .collect(Collectors.toList());

        return new SliceImpl<>(responses, pageable, hasNext);
    }
}
