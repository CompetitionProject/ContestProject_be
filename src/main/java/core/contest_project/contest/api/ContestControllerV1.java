package core.contest_project.contest.api;

import core.contest_project.bookmark.dto.BookmarkStatus;
import core.contest_project.bookmark.dto.BookmarkStatusResponse;
import core.contest_project.community.post.dto.request.PostRequest;
import core.contest_project.community.post.dto.response.PostPreviewResponse;
import core.contest_project.community.post.service.PostService;
import core.contest_project.community.post.service.data.PostPreviewDomain;
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
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/contests")
public class ContestControllerV1 {

    private final ContestService contestService;
    private static final int PAGE_SIZE = 20;
    private final PostService postService;  // 2dt

    /*// 공모전 생성
    @PostMapping(value = "/{login-id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Long> createContest(
            @RequestPart("contestCreateRequest") ContestCreateRequest request,
            @RequestPart(value = "images", required = false) List<MultipartFile> images,
            @RequestPart(value = "poster") MultipartFile poster,
            @RequestPart(value = "attachments", required = false) List<MultipartFile> attachments,
            @PathVariable("login-id") Long userId
    ) {
        // 임시로
        UserDomain writer = UserDomain.builder()
                .id(userId)
                .build();

        Long contestId =
                contestService.createContest(request, images, poster, attachments, writer.getId());
        return ResponseEntity.ok(contestId);
    }*/

    //공모전 생성(file X)
    @PostMapping( "/{login-id}")
    public ResponseEntity<Long> createContest(
            @Valid @RequestBody ContestCreateRequest request,
            @PathVariable("login-id") Long userId
    ) {
        // 임시로
        UserDomain writer = UserDomain.builder()
                .id(userId)
                .build();

        Long contestId =
                contestService.createContest(request, writer.getId());
        return ResponseEntity.ok(contestId);
    }

    // 공모전 업데이트
    @PutMapping("/{contestId}/{login-id}")
    public ResponseEntity<Void> updateContest(
            @RequestBody ContestUpdateRequest request,
            @PathVariable Long contestId,
            @PathVariable("login-id") Long userId
    ) {

        // 임시로
        UserDomain writer = UserDomain.builder()
                .id(userId)
                .build();

        contestService.updateContest(contestId, request, writer.getId());
        return ResponseEntity.noContent().build();
    }

    /*// 공모전 상세 조회
    @GetMapping("/{contestId}/{login-id}")
    public ResponseEntity<ContestResponse> getContest(
            @PathVariable Long contestId,
            @PathVariable("login-id") Long userId
    ) {
        // 임시로
        UserDomain writer = UserDomain.builder()
                .id(userId)
                .build();

        ContestResponse contest = contestService.getContest(contestId, writer.getId());
        return ResponseEntity.ok(contest);
    }*/

    // 공모전 상세 조회 (정보 부분)
    @GetMapping("/{contestId}/info/{login-id}")
    public ResponseEntity<ContestResponse> getContestInfo(
            @PathVariable Long contestId,
            @PathVariable("login-id") Long userId
    ) {
        // 임시로
        UserDomain writer = UserDomain.builder()
                .id(userId)
                .build();

        ContestResponse contestInfo = contestService.getContestInfo(contestId, writer.getId());
        return ResponseEntity.ok(contestInfo);
    }

    // 공모전 상세 조회 (본문)
    @GetMapping("/{contestId}/content/{login-id}")
    public ResponseEntity<ContestContentResponse> getContestContent(
            @PathVariable Long contestId,
            @PathVariable("login-id") Long userId
    ) {
        // 임시로
        UserDomain writer = UserDomain.builder()
                .id(userId)
                .build();

        ContestContentResponse contestContent = contestService.getContestContent(contestId, writer.getId());
        return ResponseEntity.ok(contestContent);
    }

//    // 공모전 상세 조회 (팁/후기)
//    @GetMapping("/{contestId}/reviews/{login-id}")
//    public ResponseEntity<ContestReviewsResponse> getContestReviews(
//            @PathVariable Long contestId,
//            @PathVariable("login-id") Long userId
//    ) {
//        // 임시로
//        UserDomain writer = UserDomain.builder()
//                .id(userId)
//                .build();
//
//        ContestReviewsResponse contestReviews = contestService.getContestReviews(contestId, writer.getId());
//        return ResponseEntity.ok(contestReviews);
//    }



    // 필드로 공모전들 조회(미선택 시 전체)
    @GetMapping("/{login-id}")
    public ResponseEntity<Slice<ContestSimpleResponse>> getContestsByField(
            @RequestParam(required = false) List<ContestField> fields,
            @RequestParam(required = false) Long lastContestId,
            @RequestParam(required = false) ContestSortOption sortBy,
            @PathVariable("login-id") Long userId
    ) {

        List<ContestField> searchFields = (fields != null) ? fields : new ArrayList<>();

        UserDomain writer = UserDomain.builder()
                .id(userId)
                .build();

        Slice<ContestSimpleResponse> contests =
                contestService.getContestsByField(searchFields, lastContestId, PAGE_SIZE, writer.getId(), sortBy);
        return ResponseEntity.ok(contests);
    }

    @GetMapping("/{contestId}/application")
    public ResponseEntity<ContestApplicationInfo> getApplicationInfo(
            @PathVariable Long contestId
    ) {
        ContestApplicationInfo applicationInfo = contestService.getApplicationInfo(contestId);
        return ResponseEntity.ok(applicationInfo);
    }

    // 공모전 삭제
    @DeleteMapping("/{contestId}/{login-id}")
    public ResponseEntity<Void> deleteContest(
            @PathVariable Long contestId,
            @PathVariable("login-id") Long userId
    ) {
        // 임시로
        UserDomain writer = UserDomain.builder()
                .id(userId)
                .build();

        contestService.deleteContest(contestId, writer.getId());
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{contestId}/bookmark/{login-id}")
    public ResponseEntity<BookmarkStatusResponse> toggleBookmark(
            @PathVariable Long contestId,
            @PathVariable("login-id") Long userId
    ) {
        // 임시로
        UserDomain writer = UserDomain.builder()
                .id(userId)
                .build();

        BookmarkStatus status = contestService.toggleBookmark(contestId, writer.getId());
        return ResponseEntity.ok(BookmarkStatusResponse.from(status));
    }



//    //관심 분야 목록
//    @GetMapping("/fields")
//    public ResponseEntity<List<String>> getFields() {
//
//    }


    // ===== [추가][2dt] ===== //
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
