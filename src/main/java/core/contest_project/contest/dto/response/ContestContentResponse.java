package core.contest_project.contest.dto.response;

import core.contest_project.contest.entity.Contest;
import core.contest_project.file.entity.File;
import lombok.Builder;

import java.util.List;
import java.util.stream.Collectors;

@Builder
public record ContestContentResponse(
        Long contestId,
        String content,
        List<String> contentImageUrls,
        List<String> attachmentUrls
) {
    public static ContestContentResponse from(Contest contestWithImages, Contest contestWithAttachments) {
        return ContestContentResponse.builder()
                .contestId(contestWithImages.getId())
                .content(contestWithImages.getContent())
                .contentImageUrls(contestWithImages.getContentImages().stream()
                        .map(File::getUrl)
                        .collect(Collectors.toList()))
                .attachmentUrls(contestWithAttachments.getAttachments().stream()
                        .map(File::getUrl)
                        .collect(Collectors.toList()))
                .build();
    }
}
