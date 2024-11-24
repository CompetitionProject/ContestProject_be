package core.contest_project.awaiter.individual.api;

import core.contest_project.awaiter.individual.entity.IndividualAwaiterId;
import core.contest_project.awaiter.individual.service.IndividualAwaiterService;
import core.contest_project.user.dto.response.UserBriefProfileResponse;
import core.contest_project.user.service.data.UserDomain;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Slice;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/contests/{contestId}/awaiters/individual")
public class IndividualAwaiterController {

    private final IndividualAwaiterService individualAwaiterService;
    private static final int PAGE_SIZE = 20;

    // 개인 대기자 등록
    @PostMapping
    public ResponseEntity<IndividualAwaiterId> registerIndividualAwaiter(
            @PathVariable Long contestId,
            @AuthenticationPrincipal UserDomain user
    ) {
        IndividualAwaiterId awaiterId =
                individualAwaiterService.registerIndividualAwaiter(contestId, user.getId());

        return ResponseEntity.ok(awaiterId);
    }

    // 개인 대기자 취소
    @DeleteMapping
    public ResponseEntity<Void> cancelIndividualAwaiter(
            @PathVariable Long contestId,
            @AuthenticationPrincipal UserDomain user
    ) {
        individualAwaiterService.cancelIndividualAwaiter(contestId, user.getId());
        return ResponseEntity.noContent().build();
    }

    // 개인 대기자 목록 조회
    @GetMapping
    public ResponseEntity<Slice<UserBriefProfileResponse>> getIndividualAwaiters(
            @PathVariable Long contestId,
            @RequestParam(required = false) LocalDateTime cursorDateTime
    ) {
        Slice<UserBriefProfileResponse> awaiters =
                individualAwaiterService.getIndividualAwaiters(contestId, cursorDateTime, PAGE_SIZE);
        return ResponseEntity.ok(awaiters);
    }
}
