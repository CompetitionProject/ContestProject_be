package core.contest_project.user.api;

import core.contest_project.community.post.dto.response.PostPreviewResponse;
import core.contest_project.community.post.service.data.PostSortType;
import core.contest_project.refreshtoken.common.AccessAndRefreshToken;
import core.contest_project.user.dto.request.SignUpRequest;
import core.contest_project.user.dto.request.UserDetailRequest;
import core.contest_project.user.dto.request.UserUpdateRequest;
import core.contest_project.user.dto.response.MyCommentResponse;
import core.contest_project.user.dto.response.UserBriefProfileResponse;
import core.contest_project.user.dto.response.UserProfileResponse;
import core.contest_project.user.dto.response.UserResponse;
import core.contest_project.user.service.UserService;
import core.contest_project.user.service.data.UserDomain;
import core.contest_project.user.service.data.UserInfo;
import core.contest_project.user_detail.service.UserDetailInfo;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Slice;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


@RestController
@Slf4j
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @PostMapping("/api/users/signup")
    public ResponseEntity<Map> signup(@Valid @RequestBody SignUpRequest request) {
        UserInfo user = request.toUserInfo();
        AccessAndRefreshToken tokens = userService.signup(user);

        Map<String, String> apiResponse = new ConcurrentHashMap<>();

        apiResponse.put("accessToken", tokens.accessToken());
        apiResponse.put("refreshToken", tokens.refreshToken());

        return ResponseEntity.ok().body(apiResponse);
    }

    @PostMapping("/api/users/details")
    public ResponseEntity<Void> registerUserDetails(@RequestBody UserDetailRequest request,
                                                    @AuthenticationPrincipal UserDomain loginUser) {
        UserDetailInfo userDetailInfo = request.toUserDetailInfo();
        userService.registerUserDetails(loginUser, userDetailInfo);
        return ResponseEntity.ok().build();
    }



    @PostMapping("/api/my/withdraw")
    public ResponseEntity<Void> withdraw(@AuthenticationPrincipal UserDomain loginUser){

        userService.withdraw(loginUser);
        return ResponseEntity.ok().build();
    }


    @GetMapping("/api/my/profile")
    public ResponseEntity<UserResponse> getProfile(@AuthenticationPrincipal UserDomain loginUser) {


        UserDomain user = userService.getUser(loginUser);

        UserResponse response = UserResponse.from(user);

        return ResponseEntity.ok(response);
    }


    @GetMapping("/api/my/activity/scrap")
    public ResponseEntity<Slice<PostPreviewResponse>> getScrapedPosts(
                                                                      @RequestParam(value = "page", required = false) Integer page,
                                                                      @AuthenticationPrincipal UserDomain user,
                                                                      @RequestParam(value = "sort", required = false) PostSortType sort){
        if(sort==null){sort=PostSortType.DATE;}
        if(page==null){page=0;}

        /*// 임시로
        UserDomain user = UserDomain.builder()
                .id(userId)
                .build();*/

        log.info("user.get()= {}", user.getId());

        Slice<PostPreviewResponse> posts = userService.getScrapedPosts(page, sort, user).map(PostPreviewResponse::from);
        return ResponseEntity.ok(posts);

    }


    @GetMapping("/api/my/activity/posts")
    public ResponseEntity<Slice<PostPreviewResponse>> getMyPosts(@AuthenticationPrincipal UserDomain loginUser,
                                                                 @RequestParam("page") Integer page){
        Slice<PostPreviewResponse> postPreviewResponseSlice = userService.getMyPosts(page, loginUser).map(PostPreviewResponse::from);
        return ResponseEntity.ok(postPreviewResponseSlice);
    }


    @GetMapping("/api/my/activity/comments")
    public ResponseEntity<Slice<MyCommentResponse>> getMyComments(@AuthenticationPrincipal UserDomain loginUser,
                                                                  @RequestParam(value = "page", required = false) Integer page){

        if (page == null) {
            page = 0;
        }

        Slice<MyCommentResponse> map = userService.getMyComments(loginUser, page).map(MyCommentResponse::from);
        return ResponseEntity.ok(map);
    }

    @PutMapping("/api/my/profile")
    public ResponseEntity<Void> updateProfile(@RequestBody UserUpdateRequest request,
                                              @AuthenticationPrincipal UserDomain loginUser){
        UserInfo userInfo = request.toUserInfo();
        UserDetailInfo userDetailInfo = request.toUserDetailInfo();
        userService.update(loginUser, userInfo, userDetailInfo);


        return ResponseEntity.ok().build();
    }

    @PostMapping("/api/my/logout")
    public ResponseEntity<Void> logout(@AuthenticationPrincipal UserDomain loginUser){

        userService.logout(loginUser);

        return ResponseEntity.ok().build();

    }

    // 사용자 프로필 조회
    //
    @GetMapping("/api/users/profile/{targetId}")
    public ResponseEntity<UserProfileResponse> getUserProfile(
            @PathVariable Long targetId
    ) {
        UserDomain userProfile =
                userService.getUserProfile(targetId);

        return ResponseEntity.ok(UserProfileResponse.from(userProfile));
    }

    @GetMapping("/api/users/{targetCode}")
    public ResponseEntity<UserBriefProfileResponse> getUserByUserCode(
            @PathVariable String targetCode
    ) {

        UserDomain userDomain = userService.getUserBriefProfileByCode(targetCode);
        return ResponseEntity.ok(UserBriefProfileResponse.from(userDomain));
    }
}
