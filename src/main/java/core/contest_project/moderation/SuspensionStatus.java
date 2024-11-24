package core.contest_project.moderation;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum SuspensionStatus {
    ACTIVE("활성"),
    SUSPENDED("정지됨"),
    BANNED("영구정지");

    private final String description;
}
