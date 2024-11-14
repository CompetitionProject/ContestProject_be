package core.contest_project.contest.dto.response;

import core.contest_project.contest.entity.Contest;
import core.contest_project.file.dto.SingleFileResponse;
import lombok.Builder;

import java.util.List;
import java.util.stream.Collectors;

@Builder
public record ContestContentResponse(
        Long contestId,
        String content,
        List<SingleFileResponse> contentImageUrls,
        List<SingleFileResponse> attachmentUrls
) {
    public static ContestContentResponse from(Contest contest) {
        return ContestContentResponse.builder()
                .contestId(contest.getId())
                .content(contest.getContent())
                .contentImageUrls(contest.getContentImages().stream()
                                .map(image -> new SingleFileResponse(image.getId(), image.getUrl()))
                                .collect(Collectors.toList()))
                .attachmentUrls(contest.getAttachments().stream()
                                .map(attachment -> new SingleFileResponse(attachment.getId(), attachment.getUrl()))
                                .collect(Collectors.toList()))
                .build();
    }
}
