package core.contest_project.user;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum ModerationType {
    WARNING("경고 메시지", 0),
    SUSPENSION_24H("커뮤니티 활동 정지 24시간", 24),
    SUSPENSION_7D("커뮤니티 활동 정지 7일", 168),  // 24 * 7
    PERMANENT_BAN("영구 정지", -1);  // 영구정지

    private final String description;
    private final int suspensionHours;

    public boolean isPermanent() {
        return this == PERMANENT_BAN;
    }

    public boolean isWarningOnly() {
        return this == WARNING;
    }
}