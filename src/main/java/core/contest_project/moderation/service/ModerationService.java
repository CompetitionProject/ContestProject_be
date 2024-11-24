package core.contest_project.moderation.service;

import core.contest_project.moderation.ModerationType;
import core.contest_project.moderation.SuspensionInfo;
import core.contest_project.moderation.SuspensionStatus;
import core.contest_project.user.service.UserRepository;
import core.contest_project.user.service.UserValidator;
import core.contest_project.user.service.data.UserDomain;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@Transactional
@RequiredArgsConstructor
public class ModerationService {

    private final UserRepository userRepository;
    private final UserValidator userValidator;

    public void applyModeration(String userCode, ModerationType type, UserDomain admin) {
        userValidator.validateAdmin(admin);
        UserDomain targetUser = userRepository.findByCode(userCode);
        updateUserSuspensionStatus(targetUser, type);
    }

    private void updateUserSuspensionStatus(UserDomain user, ModerationType type) {
        int newWarningCount = type == ModerationType.WARNING ?
                user.getWarningCount() + 1 : user.getWarningCount();

        SuspensionInfo suspensionInfo = type == ModerationType.WARNING ?
                getSuspensionInfoForWarningCount(newWarningCount) :
                switch (type) {
                    case SUSPENSION_24H -> new SuspensionInfo(
                            SuspensionStatus.SUSPENDED,
                            LocalDateTime.now().plusHours(24)
                    );
                    case SUSPENSION_7D -> new SuspensionInfo(
                            SuspensionStatus.SUSPENDED,
                            LocalDateTime.now().plusHours(168)
                    );
                    case PERMANENT_BAN -> new SuspensionInfo(
                            SuspensionStatus.BANNED,
                            null
                    );
                    default -> throw new IllegalArgumentException("Invalid moderation type");
                };

        userRepository.updateSuspensionStatus(
                user.getId(),
                suspensionInfo.status(),
                suspensionInfo.endTime(),
                newWarningCount
        );
    }

    private SuspensionInfo getSuspensionInfoForWarningCount(int warningCount) {
        return switch (warningCount) {
            case 1 -> new SuspensionInfo(SuspensionStatus.ACTIVE, null);
            case 2 -> new SuspensionInfo(
                    SuspensionStatus.SUSPENDED,
                    LocalDateTime.now().plusHours(24)
            );
            case 3 -> new SuspensionInfo(
                    SuspensionStatus.SUSPENDED,
                    LocalDateTime.now().plusHours(168)
            );
            default -> new SuspensionInfo(SuspensionStatus.BANNED, null);
        };
    }
}
