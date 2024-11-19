package core.contest_project.user.dto.response;

import core.contest_project.user.service.data.UserDomain;
import core.contest_project.user_detail.service.UserDetailInfo;

public record UserResponse(
        Long id,
        String email,
        String nickname,
        String snsProfileImageUrl,
        String userField,
        String duty,

        boolean isRatingPublic,

        String teamMemberCode,
        Double rating,
        UserDetailInfo userDetail
) {

    public static UserResponse from(UserDomain user){
        return new UserResponse(
                user.getId(),
                user.getUserInfo().getEmail(),
                user.getUserInfo().getNickname(),
                user.getUserInfo().getSnsProfileImageUrl(),
                user.getUserInfo().getUserField(),
                user.getUserInfo().getDuty(),
                user.getUserInfo().isRatingPublic(),
                user.getTeamMemberCode(),
                (user.getUserInfo().isRatingPublic()) ? user.getRating() : null,
                user.getUserDetail()

        );
    }
}
