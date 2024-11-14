package core.contest_project.contest.entity;

public enum ContestField {
    PLANNING_IDEA("기획/아이디어"),
    NAMING_SLOGAN("네이밍/슬로건"),
    ADVERTISING_MARKETING("광고/마케팅"),
    DESIGN("디자인"),
    LITERATURE_SCENARIO("문학/시나리오"),
    IT_GAME("IT/게임"),
    PHOTO_VIDEO_UCC("사진/영상/UCC"),
    ARTS_SPORTS("예체능"),

    OTHER("기타");

    private final String description;

    ContestField(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}