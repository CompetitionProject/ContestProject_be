package core.contest_project.community.scrap.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class ScrapCreator {
    private final ScrapRepository scrapRepository;

    public void create(Long commentId, Long userId) {
        scrapRepository.save(commentId, userId);
    }


}
