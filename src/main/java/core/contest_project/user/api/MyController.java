package core.contest_project.user.api;

import core.contest_project.contest.dto.response.InterestContest;
import core.contest_project.user.service.MyContestService;
import core.contest_project.user.service.data.UserDomain;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Slice;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/my")
public class MyController {

    private final MyContestService myContestService;


    // 북마크한 공모전 목록 조회
    @GetMapping("/bookmarks")
    public ResponseEntity<Slice<InterestContest>> getBookmarkedContests(
            @RequestParam(required = false) LocalDateTime cursorDateTime,
            @AuthenticationPrincipal UserDomain user
    ) {
        Slice<InterestContest> contests =
                myContestService.getBookmarkedContests(user.getId(), cursorDateTime);
        return ResponseEntity.ok(contests);
    }

    // 개인 대기중인 공모전 목록 조회
    @GetMapping("/individual-awaiters")
    public ResponseEntity<Slice<InterestContest>> getIndividualWaitingContests(
            @RequestParam(required = false) LocalDateTime cursorDateTime,
            @AuthenticationPrincipal UserDomain user
    ) {
        Slice<InterestContest> contests =
                myContestService.getIndividualWaitingContests(user.getId(), cursorDateTime);
        return ResponseEntity.ok(contests);
    }

    // 팀 대기중인 공모전 목록 조회
    @GetMapping("/team-awaiters")
    public ResponseEntity<Slice<InterestContest>> getTeamWaitingContests(
            @RequestParam(required = false) LocalDateTime cursorDateTime,
            @AuthenticationPrincipal UserDomain user
    ) {
        Slice<InterestContest> contests =
                myContestService.getTeamWaitingContests(user.getId(), cursorDateTime);
        return ResponseEntity.ok(contests);
    }

    /*// 나의 받은 팀원 신청 목록 조회
    @GetMapping("/my-invitations")
    public ResponseEntity<Slice<TeamInvitationResponse>> getMyInvitations(
            @PathVariable Long contestId,
            @RequestParam(required = false) LocalDateTime cursorDateTime,
            @AuthenticationPrincipal UserDomain user
    ) {
        Slice<TeamInvitationResponse> invitations =
                teamAwaiterInvitationService.getMyInvitations(user.getId(), contestId, cursorDateTime);
        return ResponseEntity.ok(invitations);
    }*/
}
