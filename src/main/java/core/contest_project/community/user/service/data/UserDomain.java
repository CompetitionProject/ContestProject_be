package core.contest_project.community.user.service.data;


import core.contest_project.community.user.SuspensionStatus;
import core.contest_project.community.user_detail.service.UserDetailInfo;
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
    private SuspensionStatus status;
    private LocalDateTime suspensionEndTime;

    private String teamMemberCode;
    private Double rating;
    private UserDetailInfo userDetail;

    public void setUserDetail(UserDetailInfo userDetail) {
        this.userDetail = userDetail;
    }

}
