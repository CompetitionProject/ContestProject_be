package core.contest_project.user.dto.response;

import core.contest_project.user.entity.User;
import core.contest_project.user.service.data.UserDomain;
import lombok.Builder;

import java.util.Collections;
import java.util.List;

@Builder
public record UserBriefProfileResponse(
        Long userId,
        String nickname,

        String profileUrl,
        String field,
        String duty,
        List<String> techStack,
        List<String> certificates
) {
    public static UserBriefProfileResponse from(UserDomain user) {
        return UserBriefProfileResponse.builder()
                .userId(user.getId())
                .nickname(user.getUserInfo().getNickname())
                .profileUrl(user.getUserInfo().getSnsProfileImageUrl())
                .field(user.getUserInfo().getUserField())
                .duty(user.getUserInfo().getDuty())
                .techStack(user.getUserDetail() != null ?
                        user.getUserDetail().getStacks() :
                        Collections.emptyList())
                .certificates(user.getUserDetail() != null ?
                        user.getUserDetail().getCertificates() :
                        Collections.emptyList())
                .build();
    }
}
