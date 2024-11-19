package core.contest_project.awaiter.individual.entity;

import core.contest_project.contest.entity.Contest;
import core.contest_project.user.entity.User;
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
    public class IndividualAwaiter {

    @EmbeddedId
    private IndividualAwaiterId id;

    @MapsId("contestId")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "contest_id")
    private Contest contest;

    @MapsId("userId")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @Enumerated(EnumType.STRING)
    private IndividualAwaiterStatus status; // WAITING, MATCHED, CANCELLED

    private LocalDateTime createdAt;

    public IndividualAwaiter cancel() {
        return this.toBuilder()
                .status(IndividualAwaiterStatus.CANCELLED)
                .build();
    }

    public IndividualAwaiter match() {
        return this.toBuilder()
                .status(IndividualAwaiterStatus.MATCHED)
                .build();
    }

    public boolean isWaiting() {
        return this.status == IndividualAwaiterStatus.WAITING;
    }

    public boolean isCancelled() {
        return this.status == IndividualAwaiterStatus.CANCELLED;
    }

    public boolean isMatched() {
        return this.status == IndividualAwaiterStatus.MATCHED;
    }
}
