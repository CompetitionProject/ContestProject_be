package core.contest_project.team.entity.member;

import core.contest_project.common.error.team.TeamErrorResult;
import core.contest_project.common.error.team.TeamException;
import core.contest_project.team.entity.Team;
import core.contest_project.user.entity.User;
import core.contest_project.user.service.data.UserDomain;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class TeamMember {

    @EmbeddedId
    private TeamMemberId id;

    @MapsId("teamId")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "team_id")
    private Team team;

    @MapsId("userId")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @Enumerated(EnumType.STRING)
    private TeamMemberRole role; // LEADER or MEMBER

    private String teamRole;            // 팀 내 역할 (기획자, 개발자 등)

    private LocalDateTime joinedAt;

    public static TeamMember createTeamMember(Team team, User user, TeamMemberRole role) {
        return TeamMember.builder()
                .id(TeamMemberId.builder()
                        .teamId(team.getId())
                        .userId(user.getId())
                        .build())
                .team(team)
                .user(user)
                .role(role)
                .joinedAt(LocalDateTime.now())
                .build();
    }

    public void updateRole(TeamMemberRole role) {
        this.role = role;
    }

    public TeamMember transferToLeader() {
        if (this.role == TeamMemberRole.LEADER) {
            throw new TeamException(TeamErrorResult.ALREADY_LEADER);
        }

        return this.toBuilder()
                .role(TeamMemberRole.LEADER)
                .build();
    }

    public TeamMember transferToMember() {
        if (this.role != TeamMemberRole.LEADER) {
            throw new TeamException(TeamErrorResult.NOT_LEADER);
        }

        return this.toBuilder()
                .role(TeamMemberRole.MEMBER)
                .build();
    }

}
