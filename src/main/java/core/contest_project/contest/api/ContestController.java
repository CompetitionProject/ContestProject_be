package core.contest_project.contest.api;

import core.contest_project.bookmark.dto.BookmarkStatus;
import core.contest_project.bookmark.dto.BookmarkStatusResponse;
import core.contest_project.community.post.dto.request.PostRequest;
import core.contest_project.community.post.dto.response.PostPreviewResponse;
import core.contest_project.community.post.service.PostService;
import core.contest_project.contest.dto.request.ContestCreateRequest;
import core.contest_project.contest.dto.request.ContestCursor;
import core.contest_project.contest.dto.request.ContestUpdateRequest;
import core.contest_project.contest.dto.response.*;
import core.contest_project.contest.entity.ContestField;
import core.contest_project.contest.entity.ContestSortOption;
import core.contest_project.contest.service.ContestService;
import core.contest_project.file.service.FileRequest;
import core.contest_project.user.service.data.UserDomain;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Slice;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/contests")
public class ContestController {

    private final ContestService contestService;
    private final PostService postService;

    //공모전 생성(file X)
//    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<Long> createContest(
            @Valid @RequestBody ContestCreateRequest request,
            @AuthenticationPrincipal UserDomain user
    ) {

        Long contestId =
                contestService.createContest(request, user);
        return ResponseEntity.ok(contestId);
    }

    // 공모전 업데이트
    @PutMapping("/{contestId}")
//    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> updateContest(
            @RequestBody ContestUpdateRequest request,
            @PathVariable Long contestId,
            @AuthenticationPrincipal UserDomain user
    ) {

        contestService.updateContest(contestId, request, user);
        return ResponseEntity.noContent().build();
    }

    // 공모전 상세 조회 (정보 부분)
    @GetMapping("/{contestId}/info")
    public ResponseEntity<ContestResponse> getContestInfo(
            @PathVariable Long contestId,
            @AuthenticationPrincipal UserDomain user
    ) {
        ContestResponse contestInfo = contestService.getContestInfo(contestId, user);
        return ResponseEntity.ok(contestInfo);
    }

    // 공모전 상세 조회 (본문)
    @GetMapping("/{contestId}/content")
    public ResponseEntity<ContestContentResponse> getContestContent(
            @PathVariable Long contestId
    ) {

        ContestContentResponse contestContent = contestService.getContestContent(contestId);
        return ResponseEntity.ok(contestContent);
    }


    /*@GetMapping
    public ResponseEntity<ContestPageResponse> getContestsByField(
            @RequestParam(required = false) List<ContestField> fields,
            @RequestParam(required = false) String cursor,
            @RequestParam(required = false) ContestSortOption sortBy,
            @AuthenticationPrincipal UserDomain user
    ) {
        ContestPageResponse response =
                contestService.getContestsByField(fields, ContestCursor.decode(cursor), sortBy, user);
        return ResponseEntity.ok(response);
    }*/


    // 공모전 삭제
//    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{contestId}")
    public ResponseEntity<Void> deleteContest(
            @PathVariable Long contestId,
            @AuthenticationPrincipal UserDomain user
    ) {

        contestService.deleteContest(contestId, user);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{contestId}/bookmark")
    public ResponseEntity<BookmarkStatusResponse> toggleBookmark(
            @PathVariable Long contestId,
            @AuthenticationPrincipal UserDomain user
    ) {
        BookmarkStatus status = contestService.toggleBookmark(contestId, user);
        return ResponseEntity.ok(BookmarkStatusResponse.from(status));
    }

    // 지원 방법 안내(email or homepage)
    @GetMapping("/{contestId}/application")
    public ResponseEntity<ContestApplicationInfo> getApplicationInfo(
            @PathVariable Long contestId
    ) {
        ContestApplicationInfo applicationInfo = contestService.getApplicationInfo(contestId);
        return ResponseEntity.ok(applicationInfo);
    }


    //관심 분야 목록
    @GetMapping("/fields")
    public ResponseEntity<List<ContestFieldResponse>> getFields() {
        List<ContestFieldResponse> fields = Arrays.stream(ContestField.values())
                .map(ContestFieldResponse::from)
                .toList();

        return ResponseEntity.ok(fields);
    }

    @PostMapping("/{contestId}/tips")
    public ResponseEntity<Long> createTip(@PathVariable Long contestId,
                                          @RequestBody PostRequest request,
                                          @AuthenticationPrincipal UserDomain writer){


        List<FileRequest> files = request.files();

        Long postId = postService.createTip(request.toPostInfo(), files, writer, contestId);


        return ResponseEntity.ok(postId);
    }
    @GetMapping("/{contestId}/popular-tips")
    public ResponseEntity<Slice<PostPreviewResponse>> getPopularTips(@PathVariable Long contestId,
                                                                     @RequestParam Integer page){


        Slice<PostPreviewResponse> tips = postService.getPopularTips(page, contestId).map(PostPreviewResponse::from);

        return ResponseEntity.ok(tips);
    }

    @GetMapping("/{contestId}/recent-tips")
    public ResponseEntity<Slice<PostPreviewResponse>> getRecentTips(@PathVariable Long contestId,
                                                                    @RequestParam Integer page){

        Slice<PostPreviewResponse> tips = postService.getRecentTips(page, contestId).map(PostPreviewResponse::from);

        return ResponseEntity.ok(tips);
    }
}
