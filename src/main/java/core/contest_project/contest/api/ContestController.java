package core.contest_project.contest.api;

import core.contest_project.bookmark.dto.BookmarkStatus;
import core.contest_project.bookmark.dto.BookmarkStatusResponse;
import core.contest_project.community.post.dto.request.PostRequest;
import core.contest_project.community.post.dto.response.PostPreviewResponse;
import core.contest_project.community.post.service.PostService;
import core.contest_project.contest.dto.request.ContestCreateRequest;
import core.contest_project.contest.dto.request.ContestUpdateRequest;
import core.contest_project.contest.dto.response.ContestApplicationInfo;
import core.contest_project.contest.dto.response.ContestContentResponse;
import core.contest_project.contest.dto.response.ContestResponse;
import core.contest_project.contest.dto.response.ContestSimpleResponse;
import core.contest_project.contest.entity.ContestField;
import core.contest_project.contest.entity.ContestSortOption;
import core.contest_project.contest.service.ContestService;
import core.contest_project.file.service.FileRequest;
import core.contest_project.user.service.data.UserDomain;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Slice;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/contests")
public class ContestController {

    private final ContestService contestService;
    private final PostService postService;
    private static final int PAGE_SIZE = 20;

    //공모전 생성(file X)
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

//    // 공모전 상세 조회 (팁/후기)
//    @GetMapping("/{contestId}/reviews")
//    public ResponseEntity<ContestReviewsResponse> getContestReviews(
//            @PathVariable Long contestId,
//            @AuthenticationPrincipal UserDomain user
//    ) {
//        ContestReviewsResponse contestReviews = contestService.getContestReviews(contestId, user.getId());
//        return ResponseEntity.ok(contestReviews);
//    }



    // 필드로 공모전들 조회(미선택 시 전체)
    @GetMapping
    public ResponseEntity<Slice<ContestSimpleResponse>> getContestsByField(
            @RequestParam(required = false) List<ContestField> fields,
            @RequestParam(required = false) Long lastContestId,
            @RequestParam(required = false) ContestSortOption sortBy,
            @AuthenticationPrincipal UserDomain user
    ) {

        List<ContestField> searchFields = (fields != null) ? fields : new ArrayList<>();

        Slice<ContestSimpleResponse> contests =
                contestService.getContestsByField(searchFields, lastContestId, PAGE_SIZE, user, sortBy);
        return ResponseEntity.ok(contests);
    }

    // 공모전 삭제
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


//    //관심 분야 목록
//    @GetMapping("/fields")
//    public ResponseEntity<List<String>> getFields() {
//
//    }

    @PostMapping("/{contestId}/tips")
    public ResponseEntity<Long> createTip(@PathVariable Long contestId,
                                          @RequestBody PostRequest request,
                                          @RequestParam Long userId){

        // 임시로
        UserDomain writer = UserDomain.builder()
                .id(userId)
                .build();

        List<FileRequest> files = request.files();

        Long postId = postService.createTip(request.toPostInfo(), files, writer, contestId);


        return ResponseEntity.ok(postId);
    }

    @GetMapping("/{contestId}/popular-tips")
    public ResponseEntity<Slice<PostPreviewResponse>> getPopularTips(@PathVariable Long contestId,
                                                                     @RequestParam Long userId,
                                                                     @RequestParam Integer page){
        // 임시로
        UserDomain writer = UserDomain.builder()
                .id(userId)
                .build();

        Slice<PostPreviewResponse> tips = postService.getPopularTips(page, contestId).map(PostPreviewResponse::from);

        return ResponseEntity.ok(tips);
    }

    @GetMapping("/{contestId}/recent-tips")
    public ResponseEntity<Slice<PostPreviewResponse>> getRecentTips(@PathVariable Long contestId,
                                                                    @RequestParam Long userId,
                                                                    @RequestParam Integer page){
        // 임시로
        UserDomain writer = UserDomain.builder()
                .id(userId)
                .build();

        Slice<PostPreviewResponse> tips = postService.getRecentTips(page, contestId).map(PostPreviewResponse::from);

        return ResponseEntity.ok(tips);
    }
}
