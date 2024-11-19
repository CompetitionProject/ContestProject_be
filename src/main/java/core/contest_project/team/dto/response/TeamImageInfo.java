package core.contest_project.team.dto.response;

import java.util.List;

public sealed interface TeamImageInfo {
    record TeamImage(String imageUrl) implements TeamImageInfo {
        public static TeamImage from(String imageUrl) {
            return new TeamImage(imageUrl);
        }
    }

    record MemberImages(List<String> profileUrls) implements TeamImageInfo {
        public static MemberImages from(List<String> profileUrls) {
            return new MemberImages(profileUrls);
        }
    }
}
