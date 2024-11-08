package core.contest_project.community.scrap.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class ScrapReader {
    private final ScrapRepository scrapRepository;

    public boolean isLiked(Long postId, Long userId ) {
        return scrapRepository.isLiked(postId, userId);

    }


    public Long count(Long postId) {
        return scrapRepository.count(postId);
    }
}
