package core.contest_project.team.service;

import core.contest_project.common.error.team.TeamErrorResult;
import core.contest_project.common.error.team.TeamException;
import core.contest_project.common.error.user.UserErrorResult;
import core.contest_project.common.error.user.UserException;
import core.contest_project.team.dto.request.TeamCreateRequest;
import core.contest_project.team.dto.response.*;
import core.contest_project.team.entity.Team;
import core.contest_project.team.entity.join.RequestStatus;
import core.contest_project.team.entity.join.TeamJoinRequest;
import core.contest_project.team.entity.join.TeamJoinRequestId;
import core.contest_project.team.entity.member.TeamMember;
import core.contest_project.team.entity.member.TeamMemberId;
import core.contest_project.team.entity.member.TeamMemberRole;
import core.contest_project.team.repository.TeamJoinRequestRepository;
import core.contest_project.team.repository.TeamMemberRepository;
import core.contest_project.team.repository.TeamRepository;
import core.contest_project.user.dto.response.UserBriefProfileResponse;
import core.contest_project.user.entity.User;
import core.contest_project.user.repository.UserJpaRepository;
import core.contest_project.user.service.UserService;
import core.contest_project.user.service.data.UserDomain;
import core.contest_project.user_detail.service.UserDetailInfo;
import core.contest_project.user_detail.service.UserDetailService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class TeamService {

    private static final int PAGE_SIZE = 20;

    private final TeamRepository teamRepository;
    private final TeamMemberRepository teamMemberRepository;
    private final TeamValidator teamValidator;
    private final UserJpaRepository userRepository;
    private final TeamJoinRequestRepository teamJoinRequestRepository;
    private final UserService userService;
    private final UserDetailService userDetailService;

    public Long createTeam(TeamCreateRequest request, UserDomain user) {

        Team team = Team.createTeam(request, user);
        team = teamRepository.save(team);

        //팀장은 만든 사람
        TeamMember leaderMember = TeamMember.createTeamMember(team, User.from(user), TeamMemberRole.LEADER);
        teamMemberRepository.save(leaderMember);

        return team.getId();
    }

    @Transactional
    public void deleteTeam(Long teamId, Long userId) {
        Team team = findTeamById(teamId);

        // 팀장 권한 확인
        teamValidator.validateLeader(team, userId);

        // 팀 가입 신청 삭제
        teamJoinRequestRepository.deleteAllByTeamId(teamId);

        // 팀원 관계 삭제
        teamMemberRepository.deleteAllByTeamId(teamId);

        // 팀 삭제
        teamRepository.delete(team);
    }
    @Transactional(readOnly = true)
    public TeamResponse getTeamProfile(Long teamId, UserDomain currentUser) {
        Team team = teamRepository.findByIdWithLeaderAndMembers(teamId)
                .orElseThrow(() -> new TeamException(TeamErrorResult.TEAM_NOT_FOUND));

        // 팀장, 멤버 상세 정보 조회
        UserDomain leaderDomain = userService.getUserProfile(team.getLeader().getId());
        List<UserDomain> memberDomains = team.getMembers().stream()
                .filter(member -> member.getRole() != TeamMemberRole.LEADER)
                .map(member -> userService.getUserProfile(member.getUser().getId()))
                .toList();

        boolean hasRequestedToJoin = false;
        if (currentUser != null) {
            // 복합키로 존재 여부 확인
            TeamJoinRequestId requestId = new TeamJoinRequestId(teamId, currentUser.getId());
            hasRequestedToJoin = teamJoinRequestRepository.existsById(requestId) &&
                    teamJoinRequestRepository.existsByTeamIdAndUserIdAndStatus(
                            teamId,
                            currentUser.getId(),
                            RequestStatus.PENDING
                    );
        }

        boolean isLeader = currentUser != null && Objects.equals(team.getLeader().getId(), currentUser.getId());
        boolean isMember = team.getMembers().stream()
                .anyMatch(member -> currentUser != null &&
                        Objects.equals(member.getUser().getId(), currentUser.getId()));

        return TeamResponse.from(team, leaderDomain, memberDomains, isLeader, isMember, hasRequestedToJoin);
    }

    public void updateTeamName(Long teamId, Long userId, String name) {
        Team team = findTeamById(teamId);
        teamValidator.validateLeader(team, userId);
        Team updatedTeam = team.updateName(name);
        teamRepository.save(updatedTeam);
    }

    public void updateTeamDescription(Long teamId, Long userId, String description) {
        Team team = findTeamById(teamId);
        teamValidator.validateLeader(team, userId);
        Team updatedTeam = team.updateDescription(description);
        teamRepository.save(updatedTeam);
    }

    public void updateTeamProfileImage(Long teamId, Long userId, String profileImageUrl) {
        Team team = findTeamById(teamId);
        teamValidator.validateLeader(team, userId);
        Team updatedTeam = team.updateProfileImage(profileImageUrl);
        teamRepository.save(updatedTeam);
    }



    // 가입 신청
    public void joinRequest(Long teamId, UserDomain user) {
        Team team = findTeamById(teamId);

        teamValidator.validateNotTeamMember(teamId, user.getId());

        TeamJoinRequestId requestId = new TeamJoinRequestId(teamId, user.getId());
        if (teamJoinRequestRepository.existsPendingById(requestId)) {
            throw new TeamException(TeamErrorResult.ALREADY_REQUESTED);
        }

        TeamJoinRequest request = TeamJoinRequest.createRequest(team, user);
        teamJoinRequestRepository.save(request);
    }

    // 가입 신청 취소
    public void cancelRequest(Long teamId, Long userId) {
        TeamJoinRequest request = findTeamJoinRequestById(teamId, userId);
        teamValidator.validateRequestPending(request.getStatus());
        teamJoinRequestRepository.delete(request);
    }

    // 가입 신청 수락
    public void acceptRequest(Long teamId, Long targetId, Long leaderId) {
        Team team = findTeamById(teamId);

        teamValidator.validateLeader(team, leaderId);

        TeamJoinRequest request = findTeamJoinRequestById(teamId, targetId);

        teamValidator.validateRequestPending(request.getStatus());

        TeamMemberId memberId = new TeamMemberId(teamId, targetId);
        TeamMember newMember = TeamMember.builder()
                .id(memberId)
                .team(team)
                .user(request.getUser())
                .role(TeamMemberRole.MEMBER)
                .joinedAt(LocalDateTime.now())
                .build();

        teamMemberRepository.save(newMember);
        request.accept();
    }

    // 가입 신청 거절
    public void rejectRequest(Long teamId, Long targetId, Long leaderId) {
        Team team = findTeamById(teamId);

        teamValidator.validateLeader(team, leaderId);

        TeamJoinRequest request = findTeamJoinRequestById(teamId, targetId);

        teamValidator.validateRequestPending(request.getStatus());

        request.reject();
    }

    // 가입 신청 목록 조회
    @Transactional(readOnly = true)
    public Slice<UserBriefProfileResponse> getTeamJoinRequests(
            Long teamId,
            Long userId,
            LocalDateTime cursorDateTime
    ) {
        Team team = findTeamById(teamId);

        // 리더나 팀 멤버인지 검증
        TeamMemberId memberId = new TeamMemberId(teamId, userId);
        if (!teamMemberRepository.existsById(memberId)) {
            throw new TeamException(TeamErrorResult.UNAUTHORIZED_ACTION);
        }

        Pageable pageable = PageRequest.of(0, PAGE_SIZE + 1);
        List<TeamJoinRequest> requests = teamJoinRequestRepository.findPendingRequests(
                teamId,
                cursorDateTime,
                pageable
        );

        boolean hasNext = requests.size() > PAGE_SIZE;
        List<TeamJoinRequest> content = hasNext ?
                requests.subList(0, PAGE_SIZE) :
                requests;

        // UserDomain 리스트 생성 및 UserDetail 정보 설정
        List<UserDomain> users = content.stream()
                .map(request -> {
                    UserDomain userDomain = request.getUser().toDomain();
                    UserDetailInfo userDetail = userDetailService.getUserDetail(userDomain);
                    return userDomain.withUserDetail(userDetail);
                })
                .toList();

        List<UserBriefProfileResponse> responses = users.stream()
                .map(UserBriefProfileResponse::from)
                .toList();

        return new SliceImpl<>(responses, pageable, hasNext);
    }

    // 팀원 강퇴
    public void expelMember(Long teamId, Long memberId, Long leaderId) {
        Team team = findTeamById(teamId);

        teamValidator.validateLeader(team, leaderId);
        teamValidator.validateTeamMember(team, memberId);
        teamValidator.validateNotLeader(team, memberId);

        deleteTeamMemberById(teamId, memberId);
    }


    // 팀 탈퇴
    public void leaveTeam(Long teamId, Long userId) {
        Team team = findTeamById(teamId);

        teamValidator.validateTeamMember(team, userId);
        teamValidator.validateNotTeamLeader(team, userId);

        deleteTeamMemberById(teamId, userId);
    }

    // 팀장 위임
    public void transferLeadership(Long newLeaderId, Long teamId, Long currentLeaderId) {
        Team team = findTeamById(teamId);

        teamValidator.validateLeader(team, currentLeaderId);
        teamValidator.validateTeamMember(team, newLeaderId);

        TeamMember newLeaderMember = findTeamMemberById(teamId, newLeaderId);
        TeamMember currentLeaderMember = findTeamMemberById(teamId, currentLeaderId);

        newLeaderMember = newLeaderMember.transferToLeader();
        currentLeaderMember = currentLeaderMember.transferToMember();
        team = team.transferLeadershipTo(newLeaderMember.getUser(), currentLeaderMember.getUser());

        teamMemberRepository.save(newLeaderMember);
        teamMemberRepository.save(currentLeaderMember);
        teamRepository.save(team);
    }

    // 팀원 추가
    public TeamMemberId addMember(Long teamId, String targetCode, Long leaderId) {
        Team team = findTeamById(teamId);

        teamValidator.validateLeader(team, leaderId);

        User targetUser = findUserByCode(targetCode);

        teamValidator.validateNotTeamMember(teamId, targetUser.getId());

        TeamMemberId memberId = new TeamMemberId(teamId, targetUser.getId());
        TeamMember newMember = TeamMember.builder()
                .id(memberId)
                .team(team)
                .user(targetUser)
                .role(TeamMemberRole.MEMBER)
                .joinedAt(LocalDateTime.now())
                .build();

        teamMemberRepository.save(newMember);

        return memberId;
    }

    @Transactional(readOnly = true)
    public List<TeamProfileResponse> getRecentTeamProfiles(Long userId) {
        List<Team> teams = teamRepository.findTop3ByUserIdOrderByJoinedAtDesc(userId);

        return teams.stream()
                .map(team -> {
                    List<String> profileUrls = new ArrayList<>();

                    String leaderProfileUrl = team.getLeader().getSnsProfileImageUrl();
                    if (leaderProfileUrl != null) {
                        profileUrls.add(leaderProfileUrl);
                    }

                    profileUrls.addAll(
                            team.getMembers().stream()
                                    .map(member -> member.getUser().getSnsProfileImageUrl())
                                    .filter(Objects::nonNull)
                                    .toList()
                    );

                    return TeamProfileResponse.from(team, profileUrls);
                })
                .toList();
    }

    public Slice<TeamBriefProfileResponse> getMyTeams(Long userId, LocalDateTime cursorDateTime, int size) {
        // size + 1로 다음 페이지 존재 여부 확인
        PageRequest pageRequest = PageRequest.of(0, size + 1);

        List<Team> teams = teamRepository.findAllByUserId(userId, cursorDateTime, pageRequest);

        boolean hasNext = teams.size() > size;
        List<Team> content = hasNext ? teams.subList(0, size) : teams;

        List<TeamBriefProfileResponse> responses = content.stream()
                .map(team -> TeamBriefProfileResponse.builder()
                        .teamId(team.getId())
                        .name(team.getName())
                        .description(team.getDescription())
                        .build())
                .toList();

        return new SliceImpl<>(responses, pageRequest, hasNext);
    }

    @Transactional(readOnly = true)
    public Slice<MyTeamJoinRequestResponse> getMyJoinRequests(Long userId, LocalDateTime cursorDateTime) {
        Pageable pageable = PageRequest.of(0, PAGE_SIZE + 1);

        List<TeamJoinRequest> requests = teamJoinRequestRepository
                .findMyRequests(userId, cursorDateTime, pageable);

        boolean hasNext = requests.size() > PAGE_SIZE;
        List<TeamJoinRequest> content = hasNext ?
                requests.subList(0, PAGE_SIZE) : requests;

        List<MyTeamJoinRequestResponse> responses = content.stream()
                .map(MyTeamJoinRequestResponse::from)
                .toList();

        return new SliceImpl<>(responses, pageable, hasNext);
    }

    @Transactional(readOnly = true)
    public List<TeamSimpleResponse> getLeadingTeams(Long userId) {
        List<Team> teams = teamRepository.findAllByLeaderId(userId);

        return teams.stream()
                .map(TeamSimpleResponse::from)
                .collect(Collectors.toList());
    }

    public Team findTeamById(Long teamId) {
        return teamRepository.findById(teamId)
                .orElseThrow(() -> new TeamException(TeamErrorResult.TEAM_NOT_FOUND));
    }

    public TeamMember findTeamMemberById(Long teamId, Long userId) {
        TeamMemberId newLeaderMemberId = new TeamMemberId(teamId, userId);
        return teamMemberRepository.findById(newLeaderMemberId)
                .orElseThrow(() -> new TeamException(TeamErrorResult.MEMBER_NOT_FOUND));
    }

    public TeamJoinRequest findTeamJoinRequestById(Long teamId, Long userId) {
        TeamJoinRequestId requestId = new TeamJoinRequestId(teamId, userId);
        return teamJoinRequestRepository.findById(requestId)
                .orElseThrow(() -> new TeamException(TeamErrorResult.JOIN_REQUEST_NOT_FOUND));
    }

    public User findUserByCode(String targetCode) {
        return userRepository.findByTeamMemberCode(targetCode)
                .orElseThrow(() -> new UserException(UserErrorResult.INVALID_MEMBER_CODE));
    }


    public void deleteTeamMemberById(Long teamId, Long userId) {
        TeamMemberId memberId = new TeamMemberId(teamId, userId);
        teamMemberRepository.deleteById(memberId);
    }
}
