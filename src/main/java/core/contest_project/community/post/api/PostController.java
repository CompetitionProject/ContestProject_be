package core.contest_project.community.post.api;

import core.contest_project.file.service.FileRequest;
import core.contest_project.community.post.dto.request.PostRequest;
import core.contest_project.community.post.dto.response.PostPreviewResponse;
import core.contest_project.community.post.dto.response.PostResponse;
import core.contest_project.community.post.service.PostService;
import core.contest_project.community.post.service.data.PostDomain;
import core.contest_project.community.post.service.data.PostSortType;
import core.contest_project.user.service.data.UserDomain;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Slice;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


@RestController
@RequiredArgsConstructor
@Slf4j
public class PostController {
    private final PostService postService;

    @PostMapping("/api/community/posts/new")
    public ResponseEntity<Map> writer(@Valid @RequestBody PostRequest request,
                                      @AuthenticationPrincipal UserDomain writer){

        List<FileRequest> files = request.files();

        Long postId = postService.write(request.toPostInfo(), files, writer);

        Map<String, Long > apiResponse=new ConcurrentHashMap<>();
        apiResponse.put("postId", postId);

        return ResponseEntity.ok().body(apiResponse);
    }



    @GetMapping("/api/community/posts/{post-id}")
    public ResponseEntity<PostResponse> getPost(@PathVariable("post-id") Long postId,
                                                @AuthenticationPrincipal UserDomain loginUser) {

        PostDomain postDomain = postService.getPost(postId, loginUser);
        PostResponse response = PostResponse.from(postDomain);

        return ResponseEntity.ok(response);
    }

    @PutMapping("/api/community/posts/{post-id}")
    public ResponseEntity<Void> update(@PathVariable("post-id") Long postId,
                                       @Valid @RequestBody PostRequest request,
                                       @AuthenticationPrincipal UserDomain loginUser) {

        postService.update(postId, request.toPostInfo(), loginUser, request.files());

        return ResponseEntity.noContent().build();

    }

    @DeleteMapping("/api/community/posts/{post-id}")
    public ResponseEntity<Void> delete(@PathVariable("post-id") Long postId,
                                       @AuthenticationPrincipal UserDomain loginUser) {

        postService.delete(postId, loginUser);
        return ResponseEntity.noContent().build();
    }


    @GetMapping("/api/community/popular-posts")
    public ResponseEntity<Slice<PostPreviewResponse>> getPopularPosts(@RequestParam("page") Integer page) {
        Slice<PostPreviewResponse> posts = postService.getPopularPosts(page, 5).map(PostPreviewResponse::from);
        return ResponseEntity.ok(posts);
    }


    @GetMapping("/api/community/posts")
    public ResponseEntity<Page<PostPreviewResponse>> getPosts(@RequestParam(value="page", required = false) Integer page,
                                                              @RequestParam(value = "sort", required = false) PostSortType sort) {
        if(page==null){page=0;}
        if(sort==null){sort= PostSortType.LIKE;}

        Page<PostPreviewResponse> posts = postService.getPosts(page, sort).map(PostPreviewResponse::from);

        return ResponseEntity.ok(posts);
    }

}
