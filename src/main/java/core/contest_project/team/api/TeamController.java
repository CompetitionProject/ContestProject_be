package core.contest_project.team.api;

import core.contest_project.team.dto.request.*;
import core.contest_project.team.dto.response.MyTeamJoinRequestResponse;
import core.contest_project.team.dto.response.TeamBriefProfileResponse;
import core.contest_project.team.dto.response.TeamProfileResponse;
import core.contest_project.team.dto.response.TeamResponse;
import core.contest_project.team.entity.member.TeamMemberId;
import core.contest_project.team.service.TeamService;
import core.contest_project.user.dto.response.UserBriefProfileResponse;
import core.contest_project.user.service.data.UserDomain;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Slice;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/teams")
public class TeamController {

    private final TeamService teamService;

    // 팀 프로필 조회
    @GetMapping("/{teamId}")
    public ResponseEntity<TeamResponse> getTeamProfile(
            @PathVariable Long teamId,
            @AuthenticationPrincipal UserDomain user
    ) {
        TeamResponse teamProfile =
                teamService.getTeamProfile(teamId, user);

        return ResponseEntity.ok(teamProfile);
    }

    // 팀 생성
    @PostMapping
    public ResponseEntity<Long> createTeam(
            @RequestBody TeamCreateRequest request,
            @AuthenticationPrincipal UserDomain user
    ) {
        Long teamId =
                teamService.createTeam(request,user);
        return ResponseEntity.ok(teamId);
    }

    // 부분 수정으로 변경
    // 이름 수정
    @PatchMapping("/{teamId}/name")
    public ResponseEntity<Void> updateTeamName(
            @PathVariable Long teamId,
            @RequestBody TeamNameUpdateRequest request,
            @AuthenticationPrincipal UserDomain user
    ) {
        teamService.updateTeamName(teamId, user.getId(), request.name());
        return ResponseEntity.noContent().build();
    }

    // 설명 수정
    @PatchMapping("/{teamId}/description")
    public ResponseEntity<Void> updateTeamDescription(
            @PathVariable Long teamId,
            @RequestBody TeamDescriptionUpdateRequest request,
            @AuthenticationPrincipal UserDomain user
    ) {
        teamService.updateTeamDescription(teamId, user.getId(), request.description());
        return ResponseEntity.noContent().build();
    }

    // 팀 프로필 이미지 수정
    @PatchMapping("/{teamId}/profile-image")
    public ResponseEntity<Void> updateTeamProfileImage(
            @PathVariable Long teamId,
            @RequestBody TeamProfileImageUpdateRequest request,
            @AuthenticationPrincipal UserDomain user

    ) {
        teamService.updateTeamProfileImage(teamId, user.getId(), request.profileUrl());
        return ResponseEntity.noContent().build();
    }

    // 팀 삭제
    @DeleteMapping("/{teamId}")
    public ResponseEntity<Void> deleteTeam(
            @PathVariable Long teamId,
            @AuthenticationPrincipal UserDomain user
    ) {
        teamService.deleteTeam(teamId, user.getId());
        return ResponseEntity.noContent().build();
    }

    // 팀 가입 신청
    @PostMapping("/{teamId}/requests")
    public ResponseEntity<Void> joinRequest(
            @PathVariable Long teamId,
            @AuthenticationPrincipal UserDomain user
    ) {
        teamService.joinRequest(teamId, user);
        return ResponseEntity.noContent().build();
    }

    // 팀 가입 신청 취소
    @DeleteMapping("/{teamId}/requests")
    public ResponseEntity<Void> cancelRequest(
            @PathVariable Long teamId,
            @AuthenticationPrincipal UserDomain user
    ) {
        teamService.cancelRequest(teamId, user.getId());
        return ResponseEntity.noContent().build();
    }

    // 팀 가입 신청 수락(리더)
    @PostMapping("/{teamId}/requests/{targetId}/accept")
    public ResponseEntity<Void> acceptRequest(
            @PathVariable Long teamId,
            @PathVariable Long targetId,
            @AuthenticationPrincipal UserDomain user
    ) {
        teamService.acceptRequest(teamId, targetId, user.getId());
        return ResponseEntity.ok().build();
    }

    // 팀 가입 신청 거절(리더)
    @PostMapping("/{teamId}/requests/{targetId}/reject")
    public ResponseEntity<Void> rejectRequest(
            @PathVariable Long teamId,
            @PathVariable Long targetId,
            @AuthenticationPrincipal UserDomain user
    ) {
        teamService.rejectRequest(teamId, targetId, user.getId());
        return ResponseEntity.ok().build();
    }

    // 팀 가입 신청 목록
    @GetMapping("/{teamId}/requests")
    public ResponseEntity<Slice<UserBriefProfileResponse>> getTeamJoinRequests(
            @PathVariable Long teamId,
            @RequestParam(required = false) LocalDateTime cursorDateTime,
            @AuthenticationPrincipal UserDomain user
    ) {
        Slice<UserBriefProfileResponse> requests =
                teamService.getTeamJoinRequests(teamId, user.getId(), cursorDateTime);
        return ResponseEntity.ok(requests);
    }

    // 팀원 강퇴(리더)
    @DeleteMapping("/{teamId}/members/{memberId}")
    public ResponseEntity<Void> expelMember(
            @PathVariable Long teamId,
            @PathVariable Long memberId,
            @AuthenticationPrincipal UserDomain user
    ) {
        teamService.expelMember(teamId, memberId, user.getId());
        return ResponseEntity.noContent().build();
    }


    // 팀 탈퇴
    @DeleteMapping("/{teamId}/leave")
    public ResponseEntity<Void> leaveTeam(
            @PathVariable Long teamId,
            @AuthenticationPrincipal UserDomain user
    ) {
        teamService.leaveTeam(teamId, user.getId());
        return ResponseEntity.noContent().build();
    }

    // 리더 위임(리더)
    @PatchMapping("/{teamId}/leader")
    public ResponseEntity<Void> transferLeadership(
            @PathVariable Long teamId,
            @RequestBody LeaderTransferRequest request,
            @AuthenticationPrincipal UserDomain user
    ) {
        teamService.transferLeadership(request.newLeaderId(), teamId, user.getId());
        return ResponseEntity.noContent().build();
    }

    // 팀원 추가(리더)
    @PostMapping("/{teamId}/members")
    public ResponseEntity<TeamMemberId> addMember(
            @PathVariable Long teamId,
            @RequestBody AddTeamMemberRequest request,
            @AuthenticationPrincipal UserDomain user

    ) {
        TeamMemberId newMemberId = teamService.addMember(teamId, request.targetCode(), user.getId());
        return ResponseEntity.ok(newMemberId);
    }

    // 내가 최근에 가입한 팀 3개 목록
    @GetMapping("/profiles")
    public ResponseEntity<List<TeamProfileResponse>> getRecentTeamProfiles(
            @AuthenticationPrincipal UserDomain user
    ) {
        List<TeamProfileResponse> teams = teamService.getRecentTeamProfiles(user.getId());
        return ResponseEntity.ok(teams);
    }

    // 내가 가입한 팀 전체 목록
    @GetMapping("/my-teams")
    public ResponseEntity<Slice<TeamBriefProfileResponse>> getMyTeams(
            @RequestParam(required = false) LocalDateTime cursorDateTime,
            @RequestParam(defaultValue = "20") int size,
            @AuthenticationPrincipal UserDomain user
    ) {
        Slice<TeamBriefProfileResponse> responses = teamService.getMyTeams(user.getId(), cursorDateTime, size);
        return ResponseEntity.ok(responses);
    }


    // 내 가입 신청 목록(쓸지 말쓸지 모름)??
    @GetMapping("/join-requests")
    public ResponseEntity<Slice<MyTeamJoinRequestResponse>> getMyJoinRequests(
            @RequestParam(required = false) LocalDateTime cursorDateTime,
            @AuthenticationPrincipal UserDomain user
    ) {
        Slice<MyTeamJoinRequestResponse> requests =
                teamService.getMyJoinRequests(user.getId(), cursorDateTime);
        return ResponseEntity.ok(requests);
    }
}
