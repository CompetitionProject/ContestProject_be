package core.contest_project.contest.dto.response;

import core.contest_project.contest.dto.request.ContestCursor;
import lombok.Builder;

import java.util.List;
@Builder
public record ContestPageResponse(
        List<ContestSimpleResponse> contests,
        boolean hasNext,
        String nextCursor
) {
    public static ContestPageResponse of(
            List<ContestSimpleResponse> contests,
            boolean hasNext,
            String nextCursor
    ) {
        return new ContestPageResponse(contests, hasNext, nextCursor);
    }
}
