package core.contest_project.user.dto.request;

import core.contest_project.user.Grade;
import core.contest_project.user.service.data.UserInfo;
import jakarta.validation.constraints.NotBlank;

public record SignUpRequest(
        String nickname,
        @NotBlank
        String email,
        String snsProfileImageUrl,

        String userField,
        String duty,

        String school,
        String major,
        Grade grade

) {

    public UserInfo toUserInfo() {
        return UserInfo.builder()
                .nickname(nickname)
                .email(email)
                .snsProfileImageUrl(snsProfileImageUrl)
                .userField(userField)
                .duty(duty)
                .school(school)
                .major(major)
                .grade(grade)
                .build();
    }
}
