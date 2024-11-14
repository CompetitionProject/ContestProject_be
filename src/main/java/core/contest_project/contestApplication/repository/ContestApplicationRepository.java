package core.contest_project.contestApplication.repository;

import core.contest_project.contest.entity.Contest;
import core.contest_project.contestApplication.entity.ContestApplication;
import core.contest_project.contestApplication.entity.ContestApplicationStatus;
import core.contest_project.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface ContestApplicationRepository extends JpaRepository<ContestApplication, Long> {
    boolean existsByContestAndApplicantAndStatus(Contest contest, User applicant, ContestApplicationStatus status);

    @Query("SELECT ca FROM ContestApplication ca " +
            "WHERE ca.contest.id = :contestId AND ca.applicant.id = :applicantId " +
            "AND ca.status = :status")
    Optional<ContestApplication> findByContestAndApplicant(
            @Param("contestId") Long contestId,
            @Param("applicantId") Long applicantId,
            @Param("status") ContestApplicationStatus status
    );
}
