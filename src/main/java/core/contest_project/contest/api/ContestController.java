package core.contest_project.contest.api;/*
package core.contest.contest.api;

import core.contest.contest.dto.request.ContestCreateRequest;
import core.contest.contest.service.ContestService;
import core.contest.user.service.data.UserDomain;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/contest")
public class ContestController {

    private final ContestService contestService;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Long> createContest(
            @RequestPart("contestCreateRequest") ContestCreateRequest request,
            @RequestPart(value = "images", required = false) List<MultipartFile> images,
            @RequestPart(value = "poster", required = false) MultipartFile poster,
            @RequestPart(value = "attachments", required = false) List<MultipartFile> attachments,
            @AuthenticationPrincipal UserDomain user
    ) {
        Long contestId =
                contestService.createContest(request, images, poster, attachments, user.getId());
        return ResponseEntity.ok(contestId);
    }

    @GetMapping("/{contestId}")
    public ResponseEntity<ContestDetailResponse> getContest(
            @PathVariable Long contestId,
            @AuthenticationPrincipal UserDomain user
    ) {
        ContestDetailResponse contest =
                contestService.getContest(contestId, user.getId());
        return ResponseEntity.ok(contest);
    }
}
*/
