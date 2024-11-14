package core.contest_project.contestApplication.entity;

import core.contest_project.contest.entity.Contest;
import core.contest_project.user.entity.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

import static jakarta.persistence.FetchType.LAZY;

@Entity
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ContestApplication {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "contest_application_id")
    private Long id;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "contest_id")
    private Contest contest;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "contest_applicant_id")
    private User applicant;

    @Enumerated(EnumType.STRING)
    private ContestApplicationType type;

    @Enumerated(EnumType.STRING)
    private ContestApplicationStatus status;

    private LocalDateTime appliedAt;

    @Builder
    private ContestApplication(Contest contest, User applicant, ContestApplicationType type) {
        this.contest = contest;
        this.applicant = applicant;
        this.type = type;
        this.status = ContestApplicationStatus.WAITING;
        this.appliedAt = LocalDateTime.now();
    }

    public void cancel() {
        this.status = ContestApplicationStatus.CANCELED;
    }
}
