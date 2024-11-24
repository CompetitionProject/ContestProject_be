package core.contest_project.moderation.api;

import core.contest_project.moderation.ModerationType;
import core.contest_project.moderation.service.ModerationService;
import core.contest_project.user.service.data.UserDomain;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin/moderation")
//@PreAuthorize("hasRole('ADMIN')")
@RequiredArgsConstructor
public class ModerationController {

    private final ModerationService moderationService;

    @PostMapping
    public ResponseEntity<Void> applyModeration(
            @RequestParam String userCode,
            @RequestParam ModerationType type,
            @AuthenticationPrincipal UserDomain user
            ) {
        moderationService.applyModeration(userCode, type, user);
        return ResponseEntity.ok().build();
    }


}
