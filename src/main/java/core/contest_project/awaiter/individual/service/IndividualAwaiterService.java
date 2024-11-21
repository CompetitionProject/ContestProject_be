package core.contest_project.awaiter.individual.service;

import core.contest_project.awaiter.individual.entity.IndividualAwaiter;
import core.contest_project.awaiter.individual.entity.IndividualAwaiterId;
import core.contest_project.awaiter.individual.entity.IndividualAwaiterStatus;
import core.contest_project.awaiter.individual.repository.IndividualAwaiterRepository;
import core.contest_project.common.error.awaiter.AwaiterErrorResult;
import core.contest_project.common.error.awaiter.AwaiterException;
import core.contest_project.common.error.contest.ContestErrorResult;
import core.contest_project.common.error.contest.ContestException;
import core.contest_project.contest.entity.Contest;
import core.contest_project.contest.repository.ContestRepository;
import core.contest_project.user.dto.response.UserBriefProfileResponse;
import core.contest_project.user.entity.User;
import core.contest_project.user.repository.UserJpaRepository;
import core.contest_project.user.service.UserReader;
import core.contest_project.user.service.data.UserDomain;
import core.contest_project.user_detail.service.UserDetailService;
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
public class IndividualAwaiterService {

    private final IndividualAwaiterRepository individualAwaiterRepository;
    private final ContestRepository contestRepository;
    private final UserJpaRepository userRepository;
    private final UserReader userReader;
    private final UserDetailService userDetailService;

    public IndividualAwaiterId registerIndividualAwaiter(Long contestId, Long userId) {
        Contest contest = contestRepository.findById(contestId)
                .orElseThrow(() -> new ContestException(ContestErrorResult.CONTEST_NOT_EXIST));

        if (contest.isExpired()) {
            throw new ContestException(ContestErrorResult.CONTEST_DEADLINE_EXPIRED);
        }

        if (individualAwaiterRepository.existsWaitingAwaiterByContestAndUser(contestId, userId)) {
            throw new AwaiterException(AwaiterErrorResult.ALREADY_WAITING);
        }

        User user = userRepository.getReferenceById(userId);

        IndividualAwaiterId awaiterId = IndividualAwaiterId.builder()
                .contestId(contestId)
                .userId(userId)
                .build();

        IndividualAwaiter awaiter = IndividualAwaiter.builder()
                .id(awaiterId)
                .contest(contest)
                .user(user)
                .status(IndividualAwaiterStatus.WAITING)
                .createdAt(LocalDateTime.now())
                .build();

        individualAwaiterRepository.save(awaiter);

        return awaiterId;
    }

    public void cancelIndividualAwaiter(Long contestId, Long userId) {
        IndividualAwaiterId awaiterId = IndividualAwaiterId.builder()
                .contestId(contestId)
                .userId(userId)
                .build();

        IndividualAwaiter awaiter = individualAwaiterRepository.findById(awaiterId)
                .orElseThrow(() -> new AwaiterException(AwaiterErrorResult.AWAITER_NOT_FOUND));

        if (!awaiter.isWaiting()) {
            throw new AwaiterException(AwaiterErrorResult.INVALID_AWAITER_STATUS);
        }

        individualAwaiterRepository.delete(awaiter);
    }

    public Slice<UserBriefProfileResponse> getIndividualAwaiters(
            Long contestId,
            LocalDateTime cursorDateTime,
            int pageSize
    ) {
        Pageable pageable = PageRequest.of(0, pageSize + 1);

        List<IndividualAwaiter> awaiters =
                individualAwaiterRepository.findWaitingAwaiters(contestId, cursorDateTime, pageable);

        boolean hasNext = awaiters.size() > pageSize;
        List<IndividualAwaiter> content = hasNext ?
                awaiters.subList(0, pageSize) :
                awaiters;

        // User ID 목록 추출
        List<Long> userIds = content.stream()
                .map(awaiter -> awaiter.getUser().getId())
                .toList();

        // User 정보 일괄 조회
        List<UserDomain> users = userReader.getUsersByIds(userIds);

        // UserDetail 정보 일괄 조회 및 UserDomain 생성
        List<UserDomain> usersWithDetails = userDetailService.setUserDetailsInBatch(users);

        List<UserBriefProfileResponse> responses = usersWithDetails.stream()
                .map(UserBriefProfileResponse::from)
                .toList();

        return new SliceImpl<>(responses, pageable, hasNext);
    }
}
