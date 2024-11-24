package core.contest_project.team_invitation.api;

import core.contest_project.team_invitation.dto.request.TeamInvitationRequest;
import core.contest_project.team_invitation.dto.response.TeamSentInvitationResponse;
import core.contest_project.team_invitation.service.TeamAwaiterInvitationService;
import core.contest_project.user.service.data.UserDomain;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Slice;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/teams")
public class TeamAwaiterInvitationController {

    private final TeamAwaiterInvitationService teamAwaiterInvitationService;

    // 초대하기
    @PostMapping("/{teamId}/contests/{contestId}/invitations")
    public ResponseEntity<Void> inviteAwaiter(
            @PathVariable Long teamId,
            @PathVariable Long contestId,
            @RequestBody TeamInvitationRequest request,
            @AuthenticationPrincipal UserDomain user
    ) {
        teamAwaiterInvitationService.inviteAwaiter(teamId, contestId, request.targetId(), user.getId());
        return ResponseEntity.noContent().build();
    }

    // 수락하기
    @PostMapping("/accept")
    public ResponseEntity<Void> acceptInvitation(
            @AuthenticationPrincipal UserDomain user
    ) {
        teamAwaiterInvitationService.acceptInvitation(user.getId());
        return ResponseEntity.noContent().build();
    }

    // 거절하기
    @PostMapping("/reject")
    public ResponseEntity<Void> rejectInvitation(
            @AuthenticationPrincipal UserDomain user
    ) {
        teamAwaiterInvitationService.rejectInvitation(user.getId());
        return ResponseEntity.noContent().build();
    }

    // 팀이 보낸 팀원 신청 목록 조회
    @GetMapping("/{teamId}/invitations")
    public ResponseEntity<Slice<TeamSentInvitationResponse>> getTeamSentInvitations(
            @PathVariable Long teamId,
            @RequestParam(required = false) LocalDateTime cursorDateTime,
            @AuthenticationPrincipal UserDomain user
    ) {
        Slice<TeamSentInvitationResponse> invitations =
                teamAwaiterInvitationService.getTeamSentInvitations(teamId, user.getId(), cursorDateTime);
        return ResponseEntity.ok(invitations);
    }
}
