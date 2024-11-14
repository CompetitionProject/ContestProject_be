package core.contest_project.user.service.data;

import core.contest_project.user.Grade;
import core.contest_project.user.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
public class UserInfo {
    private String email;
    private String nickname;
    private String snsProfileImageUrl;

    private Grade grade;
    private String school;
    private String major;

    private Role role;
    private String userField;
    private String duty;

    private boolean isRatingPublic;
}
