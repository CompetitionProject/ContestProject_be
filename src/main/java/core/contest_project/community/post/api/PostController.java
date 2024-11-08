package core.contest_project.community.post.api;

import core.contest_project.community.file.service.data.FileDomain;
import core.contest_project.community.post.dto.request.FileRequest;
import core.contest_project.community.post.dto.request.PostRequest;
import core.contest_project.community.post.dto.response.PostPreviewResponse;
import core.contest_project.community.post.dto.response.PostResponse;
import core.contest_project.community.post.service.PostService;
import core.contest_project.community.post.service.data.PostDomain;
import core.contest_project.community.post.service.data.PostSortType;
import core.contest_project.community.user.service.data.UserDomain;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Slice;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;


@RestController
@RequiredArgsConstructor
@Slf4j
public class PostController {
    private final PostService postService;
        // commit test!
    @PostMapping("/api/community/posts/new/{login-id}")
    public ResponseEntity<Map> writer(@PathVariable("login-id") Long loginUserId,
                                      @RequestBody PostRequest request){
        // 임시로
        UserDomain writer = UserDomain.builder()
                .id(loginUserId)
                .build();

        List<FileRequest> files = request.files();
        List<FileDomain> requestFiles = files.stream().map(FileRequest::toFileDomain).collect(Collectors.toList());

        Long postId = postService.write(request.toPostInfo(), requestFiles, writer);

        Map<String, Long > apiResponse=new ConcurrentHashMap<>();
        apiResponse.put("postId", postId);

        return ResponseEntity.ok().body(apiResponse);
    }


    @GetMapping("/api/community/posts/{post-id}/{login-id}")
    public ResponseEntity<PostResponse> getPost(@PathVariable("post-id") Long postId,
                                                @PathVariable("login-id") Long loginUserId) {
        // 임시로
        UserDomain loginUser = UserDomain.builder()
                .id(loginUserId)
                .build();

        PostDomain postDomain = postService.getPost(postId, loginUser);
        PostResponse response = PostResponse.from(postDomain);

        return ResponseEntity.ok(response);
    }

    @PutMapping("/api/community/posts/{post-id}/{login-id}")
    public ResponseEntity<Void> update(@PathVariable("post-id") Long postId,
                                       @PathVariable("login-id") Long loginUserId,
                                       @RequestBody PostRequest request) {

        // 임시로
        UserDomain loginUser = UserDomain.builder()
                .id(loginUserId)
                .build();

        List<FileRequest> files = request.files();
        List<FileDomain> requestFiles = files.stream().map(FileRequest::toFileDomain).collect(Collectors.toList());

        postService.update(postId, request.toPostInfo(), loginUser, requestFiles);

        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/api/community/posts/{post-id}/{login-id}")
    public ResponseEntity<Void> delete(@PathVariable("post-id") Long postId,
                                       @PathVariable("login-id") Long loginUserId) {

        // 임시로
        UserDomain loginUser = UserDomain.builder()
                .id(loginUserId)
                .build();


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
