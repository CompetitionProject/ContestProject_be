package core.contest_project.user.dto.response;

import core.contest_project.user.service.data.UserDomain;
import core.contest_project.user_detail.service.UserDetailInfo;
import lombok.Builder;

import java.util.List;

@Builder
public record UserDetailProfileResponse(
        Double rating,
        boolean isRatingPublic,
        List<String> contestExperiences,
        List<String> awardUrls,
        List<String> certificates,
        List<String> stacks
) {
    public static UserDetailProfileResponse from(UserDomain userDomain) {
        UserDetailInfo detailInfo = userDomain.getUserDetail();
        if (detailInfo == null) {
            return null;
        }

        return UserDetailProfileResponse.builder()
                .rating(userDomain.getRating())
                .isRatingPublic(userDomain.getUserInfo().isRatingPublic())
                .contestExperiences(detailInfo.getContestExperiences())
                .awardUrls(detailInfo.getAwardUrls())
                .certificates(detailInfo.getCertificates())
                .stacks(detailInfo.getStacks())
                .build();
    }
}
