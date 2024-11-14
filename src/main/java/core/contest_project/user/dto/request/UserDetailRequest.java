package core.contest_project.user.dto.request;


import core.contest_project.user_detail.service.UserDetailInfo;

import java.util.List;

public record UserDetailRequest(
        // 공모전 출품 경험
        List<String> contestExperiences,
        // 수상이력
        List<String> awardUrls,
        // 보유 자격증
        List<String> certificates,
        // 기술스택
        List<String> stacks
) {

    public UserDetailInfo toUserDetailInfo() {
        return UserDetailInfo.builder()
                .contestExperiences(contestExperiences)
                .awardUrls(awardUrls)
                .certificates(certificates)
                .stacks(stacks)
                .build();
    }

}
