package core.contest_project.team.entity;

import core.contest_project.common.error.team.TeamErrorResult;
import core.contest_project.common.error.team.TeamException;
import core.contest_project.team.dto.request.TeamCreateRequest;
import core.contest_project.team.entity.member.TeamMember;
import core.contest_project.team.entity.member.TeamMemberRole;
import core.contest_project.user.entity.User;
import core.contest_project.user.service.data.UserDomain;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class Team {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name; // 팀명
    private String description; // 팀 소개

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "leader_id")
    private User leader;

    @Builder.Default
    @OneToMany(mappedBy = "team")
    private List<TeamMember> members = new ArrayList<>();

    private String profileImageUrl;

    private LocalDateTime createdAt;

    public static Team createTeam(TeamCreateRequest request, UserDomain user) {
        return Team.builder()
                .name(request.name())
                .description(request.description())
                .leader(User.from(user))
                .createdAt(LocalDateTime.now())
                .profileImageUrl(request.profileImageUrl())
                .build();
    }
    public void addMember(User user, TeamMemberRole role) {
        TeamMember member = TeamMember.builder()
                .team(this)
                .user(user)
                .role(role)
                .joinedAt(LocalDateTime.now())
                .build();

        members.add(member);
    }

    public void removeMember(User user) {
        members.removeIf(member -> member.getUser().equals(user));
    }

    // 이름 수정
    public Team updateName(String name) {
        return this.toBuilder()
                .name(name)
                .build();
    }

    // 설명 수정
    public Team updateDescription(String description) {
        return this.toBuilder()
                .description(description)
                .build();
    }

    // 프로필 이미지 수정
    public Team updateProfileImage(String profileImageUrl) {
        return this.toBuilder()
                .profileImageUrl(profileImageUrl)
                .build();
    }


    public boolean isMember(Long userId) {
        return members.stream()
                .anyMatch(member -> member.getUser().getId().equals(userId));
    }

    public Team transferLeadershipTo(User newLeader, User currentLeader) {
        validateLeadershipTransfer(newLeader, currentLeader);

        return this.toBuilder()
                .leader(newLeader)
                .build();
    }

    private void validateLeadershipTransfer(User newLeader, User currentLeader) {
        if (!Objects.equals(this.leader.getId(), currentLeader.getId())) {
            throw new TeamException(TeamErrorResult.UNAUTHORIZED_ACTION);
        }
        if (newLeader == null) {
            throw new TeamException(TeamErrorResult.NEW_LEADER_REQUIRED);
        }
    }
}
