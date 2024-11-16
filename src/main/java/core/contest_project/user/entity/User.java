package core.contest_project.user.entity;

import core.contest_project.user.Grade;
import core.contest_project.user.Role;
import core.contest_project.user.SuspensionStatus;
import core.contest_project.user.service.data.UserDomain;
import core.contest_project.user.service.data.UserInfo;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

import static jakarta.persistence.EnumType.STRING;
import static jakarta.persistence.GenerationType.IDENTITY;
import static lombok.AccessLevel.PROTECTED;

@Entity
@Getter
@Builder
@NoArgsConstructor(access=PROTECTED)
@AllArgsConstructor(access=PROTECTED)
@Table(name="users")
public class User {

    @Id @GeneratedValue(strategy = IDENTITY)
    @Column(name="user_id")
    private Long id;
    private String nickname;
    private String snsProfileImageUrl;
    private String email;

    private String school; // (수정)타입
    private String major; // (수정)타입
    @Enumerated(STRING)
    private Grade grade;

    private String userField;  // 관심 분야?
    private String duty;  // (추가)역할

    private Double rating;
    private boolean isRatingPublic;  // (추가)

    @Enumerated(STRING)
    private Role role;

    private String teamMemberCode;  // (추가) 팀원 코드

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private boolean isDeleted; // (추가) 탈퇴 유무

    private boolean popularPostNotification;  // (추가) 인기글
    private boolean commentOnPostNotification;  // (추가) 댓글
    private boolean replyOnCommentNotification;   // (추가) 대댓글

    private int warningCount;
    private SuspensionStatus suspensionStatus;
    private LocalDateTime suspensionEndTime;


    public UserDomain toDomain(){
        UserInfo userInfo = UserInfo.builder()
                .email(email)
                .nickname(nickname)
                .snsProfileImageUrl(snsProfileImageUrl)
                .grade(grade)
                .school(school)
                .major(major)
                .role(role)
                .userField(userField)
                .duty(duty)
                .isRatingPublic(isRatingPublic)
                .build();

        return UserDomain.builder()
                .id(id)
                .userInfo(userInfo)
                .teamMemberCode(teamMemberCode)
                .rating(rating)
                .build();
    }

    public static User from(UserDomain domain){
        return User.builder()
                .id(domain.getId())
                .nickname(domain.getUserInfo().getNickname())
                .snsProfileImageUrl(domain.getUserInfo().getSnsProfileImageUrl())
                .build();
    }


    public void withdraw(){
        nickname="(알수없음)";
        email=null;
        snsProfileImageUrl =null;
        /*
        * 다른 것도?
        * */
    }

    public void update(UserInfo userInfo){
        nickname=userInfo.getNickname();
        snsProfileImageUrl =userInfo.getSnsProfileImageUrl();
        major =userInfo.getMajor();
        grade=userInfo.getGrade();
        userField =userInfo.getUserField();
        duty =userInfo.getDuty();
        updatedAt = LocalDateTime.now();
    }

}
