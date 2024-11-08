package core.contest_project.community.user.dto.request;

import core.contest_project.community.user.Grade;
import core.contest_project.community.user.service.data.UserInfo;
import core.contest_project.community.user_detail.service.UserDetailInfo;

import java.util.List;

public record UserUpdateRequest(
        String nickname,
        String profileImage,

        String department,
        Grade grade,

        String field,
        String jobRole,

        boolean isRatingPublic,

        // 공모전 출품 경험
        List<String> contestExperiences,
        // 수상이력
        List<String> awardUrls,
        // 보유 자격증
        List<String> certificates,
        // 기술스택
        List<String> stacks
) {
    public UserInfo toUserInfo(){
        return UserInfo.builder()
                .nickname(nickname)
                .snsProfileImageUrl(profileImage)
                .userField(field)
                .duty(jobRole)
                .major(department)
                .grade(grade)
                .isRatingPublic(isRatingPublic)
                .build();

    }

    public UserDetailInfo toUserDetailInfo(){
        return UserDetailInfo.builder()
                .contestExperiences(contestExperiences)
                .awardUrls(awardUrls)
                .certificates(certificates)
                .stacks(stacks)
                .build();
    }
}
