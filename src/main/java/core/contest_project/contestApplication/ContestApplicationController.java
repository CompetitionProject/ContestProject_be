package core.contest_project.contestApplication;

import core.contest_project.contestApplication.service.ContestApplicationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/contests")
public class ContestApplicationController {

    private final ContestApplicationService applicationService;

    @PostMapping("/{contestId}/applications/individual/{login-id}")
    public ResponseEntity<Long> applyIndividual(
            @PathVariable Long contestId,
            @PathVariable("login-id") Long userId
    ) {
        Long applicationId = applicationService.applyIndividual(contestId, userId);
        return ResponseEntity.ok(applicationId);
    }

    @DeleteMapping("/{contestId}/applications/{login-id}")
    public ResponseEntity<Void> cancelApplication(
            @PathVariable Long contestId,
            @PathVariable("login-id") Long userId
    ) {
        applicationService.cancelApplication(contestId, userId);
        return ResponseEntity.noContent().build();
    }

//    @GetMapping("/{contestId}/applications/status/{login-id}")
//    public ResponseEntity<Boolean>
}
