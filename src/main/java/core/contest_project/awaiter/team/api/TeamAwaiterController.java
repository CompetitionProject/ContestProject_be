package core.contest_project.awaiter.team.api;

import core.contest_project.awaiter.team.entity.TeamAwaiterId;
import core.contest_project.awaiter.team.service.TeamAwaiterService;
import core.contest_project.team.dto.response.TeamBriefProfileResponse;
import core.contest_project.user.service.data.UserDomain;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Slice;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

/**
 * 팀 대기자 등록
 * 팀 대기자 취소
 * 팀 대기자 목록 조회
 * 팀 프로필 조회 -- teamController
 * 팀 멤버 프로필 조회 -- teamController
 *
 */

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/contests/{contestId}/awaiters/team")
public class TeamAwaiterController {

    private final TeamAwaiterService teamAwaiterService;
    private static final int PAGE_SIZE = 20;

    // 팀 대기자 등록
    @PostMapping("/{teamId}")
    public ResponseEntity<TeamAwaiterId> registerTeamAwaiter(
            @PathVariable Long contestId,
            @PathVariable Long teamId,
            @AuthenticationPrincipal UserDomain user
    ) {

        TeamAwaiterId awaiterId =
                teamAwaiterService.registerTeamAwaiter(contestId, teamId, user.getId());

        return ResponseEntity.ok(awaiterId);
    }

    // 팀 대기자 취소
    @DeleteMapping("/{teamId}")
    public ResponseEntity<Void> cancelTeamAwaiter(
            @PathVariable Long contestId,
            @PathVariable Long teamId,
            @AuthenticationPrincipal UserDomain user
    ) {
        teamAwaiterService.cancelTeamAwaiter(contestId, teamId, user.getId());
        return ResponseEntity.noContent().build();
    }

    // 팀 대기자 목록 조회
    @GetMapping
    public ResponseEntity<Slice<TeamBriefProfileResponse>> getTeamAwaiters(
            @PathVariable Long contestId,
            @RequestParam(required = false) LocalDateTime cursorDateTime
    ) {
        Slice<TeamBriefProfileResponse> awaiters =
                teamAwaiterService.getTeamAwaiters(contestId,cursorDateTime, PAGE_SIZE);

        return ResponseEntity.ok(awaiters);
    }
}
