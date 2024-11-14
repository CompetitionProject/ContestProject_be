package core.contest_project.contestApplication.service;

import core.contest_project.common.error.contest.ContestErrorResult;
import core.contest_project.common.error.contest.ContestException;
import core.contest_project.common.error.user.UserErrorResult;
import core.contest_project.common.error.user.UserException;
import core.contest_project.contest.entity.Contest;
import core.contest_project.contest.repository.ContestRepository;
import core.contest_project.contestApplication.entity.ContestApplication;
import core.contest_project.contestApplication.entity.ContestApplicationStatus;
import core.contest_project.contestApplication.entity.ContestApplicationType;
import core.contest_project.contestApplication.repository.ContestApplicationRepository;
import core.contest_project.user.entity.User;
import core.contest_project.user.repository.UserJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class ContestApplicationService {

    private final ContestRepository contestRepository;
    private final ContestApplicationRepository applicationRepository;
    private final UserJpaRepository userRepository;

    public Long applyIndividual(Long contestId, Long userId) {
        Contest contest = contestRepository.findById(contestId)
                .orElseThrow(() -> new ContestException(ContestErrorResult.CONTEST_NOT_EXIST));

        User applicant = userRepository.findById(userId)
                .orElseThrow(() -> new UserException(UserErrorResult.USER_NOT_EXIST));

        // 이미 대기 중인지 확인
        if (applicationRepository.existsByContestAndApplicantAndStatus(
                contest, applicant, ContestApplicationStatus.WAITING)) {
            throw new ContestException(ContestErrorResult.ALREADY_WAITING);
        }

        ContestApplication application = ContestApplication.builder()
                .contest(contest)
                .applicant(applicant)
                .type(ContestApplicationType.INDIVIDUAL)
                .build();

        return applicationRepository.save(application).getId();
    }

    public void cancelApplication(Long contestId, Long userId) {
        ContestApplication application = applicationRepository
                .findByContestAndApplicant(contestId, userId, ContestApplicationStatus.WAITING)
                .orElseThrow(() -> new ContestException(ContestErrorResult.APPLICATION_NOT_FOUND));

        application.cancel();
    }

    @Transactional(readOnly = true)
    public boolean isWaiting(Long contestId, Long userId) {
        return applicationRepository.existsByContestAndApplicantAndStatus(
                contestRepository.getReferenceById(contestId),
                userRepository.getReferenceById(userId),
                ContestApplicationStatus.WAITING
        );
    }

}
