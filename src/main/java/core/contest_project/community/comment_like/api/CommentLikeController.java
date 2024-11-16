package core.contest_project.community.comment_like.api;

import core.contest_project.community.comment_like.CommentLikeStatus;
import core.contest_project.community.comment_like.service.*;
import core.contest_project.user.service.data.UserDomain;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@Slf4j
@RequiredArgsConstructor
public class CommentLikeController {
    private final CommentLikeService commentLikeService;

    @PostMapping("/api/community/comments/{comment-id}/likes")
    public ResponseEntity<CommentLikeStatus> flip(@PathVariable("comment-id") Long commentId,
                                                  @AuthenticationPrincipal UserDomain loginUser) {

        CommentLikeStatus status = commentLikeService.flip(commentId, loginUser);
        return ResponseEntity.ok(status);
    }

}
