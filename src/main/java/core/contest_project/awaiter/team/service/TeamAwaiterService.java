package core.contest_project.awaiter.team.service;

import core.contest_project.awaiter.team.entity.TeamAwaiter;
import core.contest_project.awaiter.team.entity.TeamAwaiterId;
import core.contest_project.awaiter.team.repository.TeamAwaiterRepository;
import core.contest_project.common.error.awaiter.AwaiterErrorResult;
import core.contest_project.common.error.awaiter.AwaiterException;
import core.contest_project.common.error.contest.ContestErrorResult;
import core.contest_project.common.error.contest.ContestException;
import core.contest_project.common.error.team.TeamErrorResult;
import core.contest_project.common.error.team.TeamException;
import core.contest_project.contest.entity.Contest;
import core.contest_project.contest.repository.ContestRepository;
import core.contest_project.team.dto.response.TeamBriefProfileResponse;
import core.contest_project.team.entity.Team;
import core.contest_project.team.repository.TeamRepository;
import core.contest_project.team.service.TeamValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class TeamAwaiterService {

    private final ContestRepository contestRepository;
    private final TeamRepository teamRepository;
    private final TeamAwaiterRepository teamAwaiterRepository;
    private final TeamValidator teamValidator;

    public TeamAwaiterId registerTeamAwaiter(Long contestId, Long teamId, Long userId) {
        Contest contest = contestRepository.findById(contestId)
                .orElseThrow(() -> new ContestException(ContestErrorResult.CONTEST_NOT_EXIST));

        Team team = teamRepository.findById(teamId)
                .orElseThrow(() -> new TeamException(TeamErrorResult.TEAM_NOT_FOUND));

        // 공모전 마감 여부 확인
        if (contest.isExpired()) {
            throw new ContestException(ContestErrorResult.CONTEST_DEADLINE_EXPIRED);
        }

        // 팀장 권한 확인
        teamValidator.validateLeader(team, userId);

        // 이미 대기 중인지 확인
        TeamAwaiterId awaiterId = new TeamAwaiterId(contestId, teamId);
        if (teamAwaiterRepository.existsById(awaiterId)) {
            throw new AwaiterException(AwaiterErrorResult.ALREADY_WAITING);
        }

        // 팀 대기자 생성
        TeamAwaiter awaiter = TeamAwaiter.builder()
                .id(awaiterId)
                .contest(contest)
                .team(team)
                .createdAt(LocalDateTime.now())
                .build();

        teamAwaiterRepository.save(awaiter);

        return awaiterId;
    }

    public void cancelTeamAwaiter(Long contestId, Long teamId, Long userId) {
        Team team = teamRepository.findById(teamId)
                .orElseThrow(() -> new TeamException(TeamErrorResult.TEAM_NOT_FOUND));

        // 팀장 권한 확인
        teamValidator.validateLeader(team, userId);

        TeamAwaiterId awaiterId = new TeamAwaiterId(contestId, teamId);
        TeamAwaiter awaiter = teamAwaiterRepository.findById(awaiterId)
                .orElseThrow(() -> new AwaiterException(AwaiterErrorResult.AWAITER_NOT_FOUND));

        teamAwaiterRepository.delete(awaiter);
    }

    @Transactional(readOnly = true)
    public Slice<TeamBriefProfileResponse> getTeamAwaiters(
            Long contestId,
            LocalDateTime cursorDateTime,
            int pageSize
    ) {
        Pageable pageable = PageRequest.of(0, pageSize + 1);

        List<TeamAwaiter> awaiters = teamAwaiterRepository.findTeamAwaiters(
                contestId,
                cursorDateTime,
                pageable
        );

        boolean hasNext = awaiters.size() > pageSize;
        List<TeamAwaiter> content = hasNext ?
                awaiters.subList(0, pageSize) :
                awaiters;

        List<TeamBriefProfileResponse> responses = content.stream()
                .map(awaiter -> TeamBriefProfileResponse.builder()
                        .teamId(awaiter.getTeam().getId())
                        .name(awaiter.getTeam().getName())
                        .description(awaiter.getTeam().getDescription())
                        .build())
                .toList();

        return new SliceImpl<>(responses, pageable, hasNext);
    }
}
