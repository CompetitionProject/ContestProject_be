package core.contest_project.user.dto.response;

import core.contest_project.user.Grade;
import core.contest_project.user.service.data.UserDomain;
import core.contest_project.user.service.data.UserInfo;
import lombok.Builder;

@Builder
public record UserBasicProfileResponse(
        String profileUrl,
        String nickname,
        String school,
        Grade grade,
        String major,
        String field,
        String duty
) {
    public static UserBasicProfileResponse from(UserDomain userDomain) {
        UserInfo userInfo = userDomain.getUserInfo();
        return UserBasicProfileResponse.builder()
                .profileUrl(userInfo.getSnsProfileImageUrl())
                .nickname(userInfo.getNickname())
                .school(userInfo.getSchool())
                .grade(userInfo.getGrade())
                .major(userInfo.getMajor())
                .field(userInfo.getUserField())
                .duty(userInfo.getDuty())
                .build();
    }
}
