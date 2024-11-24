package core.contest_project.contest.service;

import core.contest_project.common.error.contest.ContestErrorResult;
import core.contest_project.common.error.contest.ContestException;
import core.contest_project.contest.entity.Contest;
import core.contest_project.contest.entity.ContestStatus;
import core.contest_project.contest.repository.ContestRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class ContestStatusService {
    private final ContestRepository contestRepository;

    public ContestStatus getStatus(Long contestId) {
        Contest contest = contestRepository.findById(contestId)
                .orElseThrow(() -> new ContestException(ContestErrorResult.CONTEST_NOT_EXIST));

        return contest.getContestStatus();
    }

    @Scheduled(cron = "0 0 0 * * *") // 자정
    @Transactional
    public void updateAllContestStatuses() {
        List<Contest> activeContests = contestRepository.findActiveContests();

        int updatedCount = 0;
        for (Contest contest : activeContests) {
            ContestStatus oldStatus = contest.getContestStatus();
            contest.updateStatus();

            if (contest.getContestStatus() != oldStatus) {
                updatedCount++;
            }
        }

        contestRepository.saveAll(activeContests);
        log.info("Updated {} contest statuses out of {}", updatedCount, activeContests.size());

    }

}
