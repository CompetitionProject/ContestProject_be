package core.contest_project.user.dto.response;

import core.contest_project.user.entity.User;

public record UserChatProfileResponse(
        Long userId,
        String nickname,
        String profileUrl
) {

    public static UserChatProfileResponse from(User user){
        return new UserChatProfileResponse(user.getId(), user.getNickname(), user.getSnsProfileImageUrl());
    }

}
