package core.contest_project.community.post.service.data;

public enum PostSortType {
    DATE("createAt"),
    LIKE("likeCount"),
    VIEW("viewCount");

    private final String fieldName;

    PostSortType(String fieldName) {
        this.fieldName = fieldName;
    }

    public String getFieldName() {
        return fieldName;
    }
}

