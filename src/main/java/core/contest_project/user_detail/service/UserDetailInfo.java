package core.contest_project.user_detail.service;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
@AllArgsConstructor
public class UserDetailInfo {
    private List<String> contestExperiences;
    private List<String> awardUrls;
    private List<String> certificates;
    private List<String> stacks;
}
