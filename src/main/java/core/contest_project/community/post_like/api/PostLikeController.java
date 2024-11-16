package core.contest_project.community.post_like.api;

import core.contest_project.community.comment_like.CommentLikeStatus;
import core.contest_project.community.post_like.PostLikeStatus;
import core.contest_project.community.post_like.service.PostLikeService;
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
public class PostLikeController {
    private final PostLikeService postLikeService;

    @PostMapping("/api/community/posts/{post-id}/likes")
    public ResponseEntity<PostLikeStatus> flip(@PathVariable("post-id") Long postId,
                                               @AuthenticationPrincipal UserDomain loginUser) {

        PostLikeStatus status = postLikeService.flip(postId, loginUser);
        return ResponseEntity.ok(status);
    }


}
