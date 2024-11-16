package core.contest_project.community.comment.api;

import core.contest_project.community.comment.dto.CommentResponse;
import core.contest_project.community.comment.dto.CreateCommentRequest;
import core.contest_project.community.comment.dto.ParentCommentResponse;
import core.contest_project.community.comment.service.CommentService;
import core.contest_project.community.comment.service.date.CommentActivityDomain;
import core.contest_project.community.comment.service.date.ParentCommentDomain;
import core.contest_project.user.service.data.UserDomain;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Slice;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
public class CommentController {
    private final CommentService commentService;


    @PostMapping("/api/community/posts/{post-id}/comments")
    public ResponseEntity<Long> writeComment(@PathVariable("post-id") Long postId,
                                             @AuthenticationPrincipal UserDomain writer,
                                             @RequestBody(required = false) CreateCommentRequest request){

        Long id = commentService.write(postId, writer, request.content(), request.isReply(), request.parentId(), request.isAnonymous());
        return ResponseEntity.ok(id);
    }


    @GetMapping("/api/community/posts/{post-id}/comments")
    public ResponseEntity<CommentResponse> readComment(@PathVariable("post-id") Long postId,
                                                       @AuthenticationPrincipal UserDomain loginUser){


        List<ParentCommentDomain> parents = commentService.getParentsAndChildren(postId,loginUser);
        List<ParentCommentResponse> comments=new ArrayList<>();

        for(ParentCommentDomain parent:parents){
            comments.add(ParentCommentResponse.from(parent));
        }

        CommentResponse response = new CommentResponse(comments);


        return ResponseEntity.ok(response);
    }


    @DeleteMapping("/api/community/posts/{post-id}/comments/{comment-id}")
    public ResponseEntity<Void> deleteComment(@PathVariable("post-id") Long postId,
                                              @PathVariable("comment-id") Long commentId,
                                              @AuthenticationPrincipal UserDomain loginUser) {


        commentService.delete(commentId, loginUser);

        return ResponseEntity.noContent().build();
    }


    @GetMapping("/test/comments")
    public ResponseEntity<Slice<CommentActivityDomain>> getComments(@RequestParam("page") Integer page,
                                                                    @RequestParam("teamMemberCode")String teamMemberCode){
        Slice<CommentActivityDomain> comments = commentService.getCommentsByTeamMemberCode(teamMemberCode, page);
        return ResponseEntity.ok(comments);
    }



}
