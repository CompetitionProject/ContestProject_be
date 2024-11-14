package core.contest_project.community.comment.service.date;

import core.contest_project.user.service.data.UserDomain;
import core.contest_project.user.service.data.UserInfo;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class CommentInfo {
    private UserDomain writerDomain;

    private String content;

    private boolean isReply;
    private Long parentId;

    private boolean isAnonymous;
    private Long anonymousNumber;

    public void anonymize(){
        UserInfo userInfo = UserInfo.builder()
                .nickname("익명" + anonymousNumber)
                .snsProfileImageUrl(null)
                .build();

        writerDomain=UserDomain.builder()
                                .id(writerDomain.getId()) // 흠..
                                .userInfo(userInfo)
                                .build();

    }


}
