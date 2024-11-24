package core.contest_project.contest.dto.request;

import core.contest_project.contest.entity.Contest;
import core.contest_project.contest.entity.ContestSortOption;

import java.time.LocalDateTime;
import java.util.Base64;

import static core.contest_project.contest.dto.request.ContestCursor.SortValue.*;


public record ContestCursor(
        Long contestId, // 2차 정렬을 위한 공통 필드
        SortValue sortValue // 정렬 조건 값
) {
    public sealed interface SortValue permits Latest, Bookmarks, Deadline, Awaiters, Reviews {
        record Latest(LocalDateTime createdAt) implements SortValue{}
        record Bookmarks(Long count) implements SortValue{}
        record Deadline(LocalDateTime endDate) implements SortValue{}
        record Awaiters(Long count) implements SortValue{}
        record Reviews(Long count) implements SortValue {}
    }

    public static ContestCursor create(Contest contest, ContestSortOption sort,
                                       Long awaiterCount, Long reviewCount) {
        SortValue sortValue = switch (sort) {
            case LATEST -> new SortValue.Latest(contest.getCreatedAt());
            case MOST_BOOKMARKS -> new SortValue.Bookmarks(contest.getBookmarkCount());
            case CLOSEST_DEADLINE -> new SortValue.Deadline(contest.getEndDate());
            case MOST_AWAITERS -> new SortValue.Awaiters(awaiterCount);
            case MOST_REVIEWS -> new SortValue.Reviews(reviewCount);
        };
        return new ContestCursor(contest.getId(), sortValue);
    }

    public String encode() {
        if (sortValue instanceof SortValue.Latest latest) {
            return String.format("LATEST,%d,%s", contestId, latest.createdAt());
        } else if (sortValue instanceof SortValue.Bookmarks bookmarks) {
            return String.format("BOOKMARKS,%d,%d", contestId, bookmarks.count());
        } else if (sortValue instanceof SortValue.Deadline deadline) {
            return String.format("DEADLINE,%d,%s", contestId, deadline.endDate());
        } else if (sortValue instanceof SortValue.Awaiters awaiters) {
            return String.format("AWAITERS,%d,%d", contestId, awaiters.count());
        } else if (sortValue instanceof SortValue.Reviews reviews) {
            return String.format("REVIEWS,%d,%d", contestId, reviews.count());
        }
        throw new IllegalArgumentException("Unknown sort value type");
    }

    public static ContestCursor decode(String encoded) {
        if (encoded == null) return null;

        try {
            String decoded = new String(Base64.getDecoder().decode(encoded));
            String[] parts = decoded.split(",");

            Long contestId = Long.parseLong(parts[1]);
            return switch (parts[0]) {
                case "LATEST" -> new ContestCursor(contestId,
                        new SortValue.Latest(LocalDateTime.parse(parts[2])));
                case "BOOKMARKS" -> new ContestCursor(contestId,
                        new SortValue.Bookmarks(Long.parseLong(parts[2])));
                case "DEADLINE" -> new ContestCursor(contestId,
                        new SortValue.Deadline(LocalDateTime.parse(parts[2])));
                case "AWAITERS" -> new ContestCursor(contestId,
                        new SortValue.Awaiters(Long.parseLong(parts[2])));
                case "REVIEWS" -> new ContestCursor(contestId,
                        new SortValue.Reviews(Long.parseLong(parts[2])));
                default -> throw new IllegalArgumentException("Invalid sort type: " + parts[0]);
            };
        } catch (Exception e) {
            throw new IllegalArgumentException("Invalid cursor format", e);
        }
    }

}
