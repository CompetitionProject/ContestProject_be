package core.contest_project.contest.dto.response;

import core.contest_project.contest.entity.Contest;
import core.contest_project.contest.entity.ContestApplicationMethod;

public record ContestApplicationInfo(
        ContestApplicationMethod method,
        String applicationUrl,  // email 또는 hostUrl
        String message  // 안내 메시지
) {
    public static ContestApplicationInfo from(Contest contest) {
        String url = contest.getApplicationMethod() == ContestApplicationMethod.EMAIL
                ? contest.getApplicationEmail()
                : contest.getHostUrl();

        String msg = contest.getApplicationMethod() == ContestApplicationMethod.EMAIL
                ? "해당 공모전은 이메일로 지원이 가능합니다."
                : "해당 공모전은 홈페이지로 지원이 가능합니다.";

        return new ContestApplicationInfo(
                contest.getApplicationMethod(),
                url,
                msg
        );
    }
}
