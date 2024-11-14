package core.contest_project.contest.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum ContestSortOption {
    LATEST("최신순"),
    MOST_BOOKMARKS("북마크순"),
//    MOST_AWAITERS("팀원 모집순"),
    //    MOST_REVIEWS("후기 많은순"),
    CLOSEST_DEADLINE("마감순");

    private String description;
}