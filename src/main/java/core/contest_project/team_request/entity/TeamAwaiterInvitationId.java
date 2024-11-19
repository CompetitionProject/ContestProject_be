package core.contest_project.team_request.entity;

import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Objects;

@Embeddable
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TeamAwaiterInvitationId implements Serializable {

    private Long teamId;
    private Long targetUserId;


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TeamAwaiterInvitationId that = (TeamAwaiterInvitationId) o;
        return Objects.equals(teamId, that.teamId) &&
                Objects.equals(targetUserId, that.targetUserId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(teamId, targetUserId);
    }
}
