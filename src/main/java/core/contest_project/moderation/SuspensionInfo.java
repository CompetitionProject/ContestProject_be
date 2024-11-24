package core.contest_project.moderation;

import java.time.LocalDateTime;

public record SuspensionInfo(
        SuspensionStatus status,
        LocalDateTime endTime
) {
}
