package core.contest_project.contest.entity;

import lombok.Getter;

@Getter
public enum ContestApplicationMethod {
//    QR("QR 코드로 지원"),

    EMAIL("이메일 지원"),
    HOMEPAGE("홈페이지로 지원");

    private final String description;

    ContestApplicationMethod(String description) {
        this.description = description;
    }
}