package core.contest_project.user.dto.response;

import core.contest_project.user.service.data.UserDomain;
import lombok.Builder;


@Builder
public record UserProfileResponse(
        // 기본 정보
        UserBasicProfileResponse basicProfile,

        // 세부 정보
        UserDetailProfileResponse detailProfile

) {
    public static UserProfileResponse from(UserDomain userDomain) {
        return UserProfileResponse.builder()
                .basicProfile(UserBasicProfileResponse.from(userDomain))
                .detailProfile(UserDetailProfileResponse.from(userDomain))
                .build();
    }
}
