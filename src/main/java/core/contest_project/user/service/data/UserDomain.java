package core.contest_project.user.service.data;


import core.contest_project.moderation.SuspensionStatus;
import core.contest_project.user_detail.service.UserDetailInfo;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
@Builder
public class UserDomain {
    private Long id;
    private UserInfo userInfo;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    private int warningCount;
    private SuspensionStatus suspensionStatus;
    private LocalDateTime suspensionEndTime;

    private String teamMemberCode;
    private Double rating;
    private UserDetailInfo userDetail;

    public void setUserDetail(UserDetailInfo userDetail) {
        this.userDetail = userDetail;
    }

    public UserDomain withUserDetail(UserDetailInfo userDetail) {
        return UserDomain.builder()
                .id(this.id)
                .userInfo(this.userInfo)
                .createdAt(this.createdAt)
                .updatedAt(this.updatedAt)
                .userDetail(userDetail)  // 새로운 userDetail
                .teamMemberCode(this.teamMemberCode)
                .rating(this.rating)
                .build();
    }

    public boolean isSuspended() {
        if (suspensionStatus == SuspensionStatus.BANNED) {
            return true;
        }
        if (suspensionStatus == SuspensionStatus.SUSPENDED && suspensionEndTime != null) {
            return LocalDateTime.now().isBefore(suspensionEndTime);
        }
        return false;
    }

}
