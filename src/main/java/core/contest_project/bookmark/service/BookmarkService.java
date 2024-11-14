package core.contest_project.bookmark.service;

import core.contest_project.bookmark.dto.BookmarkStatus;
import core.contest_project.bookmark.entity.Bookmark;
import core.contest_project.bookmark.repository.BookmarkRepository;
import core.contest_project.contest.entity.Contest;
import core.contest_project.contest.repository.ContestRepository;
import core.contest_project.user.entity.User;
import core.contest_project.user.repository.UserJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class BookmarkService {

    private final BookmarkRepository bookmarkRepository;
    private final ContestRepository contestRepository;
    private final UserJpaRepository userRepository;

    @Transactional
    public BookmarkStatus toggleBookmark(Contest contest, Long userId) {

        User user = userRepository.getReferenceById(userId);

        boolean hasBookmarked = bookmarkRepository.existsByContestIdAndUserId(contest.getId(), user.getId());
        BookmarkStatus bookmarkStatus;

        if (hasBookmarked) {
            bookmarkRepository.deleteByContestIdAndUserId(contest.getId(), userId);
            bookmarkStatus = BookmarkStatus.UNBOOKMARK;
        } else {
            Bookmark newBookmark = Bookmark.createBookmark(contest, user);
            bookmarkRepository.save(newBookmark);
            bookmarkStatus = BookmarkStatus.BOOKMARK;
        }
        return bookmarkStatus;
    }

}
