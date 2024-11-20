package core.contest_project.team.entity.join;

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
@Builder
public class TeamJoinRequest {
    @EmbeddedId
    private TeamJoinRequestId id;

    @MapsId("teamId")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "team_id")
    private Team team;

    @MapsId("userId")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @Enumerated(EnumType.STRING)
    private RequestStatus status;  // PENDING, ACCEPTED, REJECTED

    private LocalDateTime createdAt;


    public static TeamJoinRequest createRequest(Team team, UserDomain user) {
        TeamJoinRequestId id = TeamJoinRequestId.builder()
                .teamId(team.getId())
                .userId(user.getId())
                .build();

        return TeamJoinRequest.builder()
                .id(id)
                .team(team)
                .user(User.from(user))
                .status(RequestStatus.PENDING)
                .createdAt(LocalDateTime.now())
                .build();
    }

    public void accept() {
        this.status = RequestStatus.ACCEPTED;
    }

    public void reject() {
        this.status = RequestStatus.REJECTED;
    }
}