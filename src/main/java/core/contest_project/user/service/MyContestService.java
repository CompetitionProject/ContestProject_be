package core.contest_project.user.service;

import core.contest_project.awaiter.individual.repository.IndividualAwaiterRepository;
import core.contest_project.awaiter.team.repository.TeamAwaiterRepository;
import core.contest_project.bookmark.repository.BookmarkRepository;
import core.contest_project.contest.dto.response.InterestContest;
import core.contest_project.contest.entity.Contest;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MyContestService {
    private final BookmarkRepository bookmarkRepository;
    private final IndividualAwaiterRepository individualAwaiterRepository;
    private final TeamAwaiterRepository teamAwaiterRepository;
    private static final int PAGE_SIZE = 20;

    public Slice<InterestContest> getBookmarkedContests(Long userId, LocalDateTime cursorDateTime) {
        Pageable pageable = PageRequest.of(0, PAGE_SIZE + 1);
        List<Contest> contests = bookmarkRepository.findBookmarkedContests(userId, cursorDateTime, pageable);
        return createSliceResponse(contests, pageable);
    }

    public Slice<InterestContest> getIndividualWaitingContests(Long userId, LocalDateTime cursorDateTime) {
        Pageable pageable = PageRequest.of(0, PAGE_SIZE + 1);
        List<Contest> contests = individualAwaiterRepository.findWaitingContests(userId, cursorDateTime, pageable);
        return createSliceResponse(contests, pageable);
    }

    public Slice<InterestContest> getTeamWaitingContests(Long userId, LocalDateTime cursorDateTime) {
        Pageable pageable = PageRequest.of(0, PAGE_SIZE + 1);
        List<Contest> contests = teamAwaiterRepository.findWaitingContests(userId, cursorDateTime, pageable);
        return createSliceResponse(contests, pageable);
    }

    private Slice<InterestContest> createSliceResponse(List<Contest> contests, Pageable pageable) {
        boolean hasNext = contests.size() > PAGE_SIZE;
        List<Contest> content = hasNext ?
                contests.subList(0, PAGE_SIZE) :
                contests;

        List<InterestContest> responses = content.stream()
                .map(this::mapToDto)
                .toList();

        return new SliceImpl<>(responses, pageable, hasNext);
    }

    private InterestContest mapToDto(Contest contest) {
        String posterUrl = contest.getContentImages() != null && !contest.getContentImages().isEmpty()
                ? contest.getContentImages().get(0).getUrl()
                : null;

        return new InterestContest(
                posterUrl,
                contest.getTitle(),
                contest.getHost(),
                contest.getEndDate().format(DateTimeFormatter.ofPattern("yyyy.MM.dd HH:mm:ss"))
        );
    }
}

