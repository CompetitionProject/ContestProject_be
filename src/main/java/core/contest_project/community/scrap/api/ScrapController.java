package core.contest_project.community.scrap.api;

import core.contest_project.community.scrap.service.ScrapRepository;
import core.contest_project.community.scrap.service.ScrapService;
import core.contest_project.community.scrap.ScrapStatus;
import core.contest_project.user.service.data.UserDomain;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
@RequiredArgsConstructor
public class ScrapController {
    private final ScrapService scrapService;
    private final ScrapRepository scrapRepository;

    @PostMapping("/api/community/posts/{post-id}/scraps")
    public ResponseEntity<ScrapStatus> flip(@PathVariable("post-id") Long postId,
                                            @AuthenticationPrincipal UserDomain loginUser) {
        ScrapStatus status = scrapService.flip(postId, loginUser);
        return ResponseEntity.ok(status);
    }


}
