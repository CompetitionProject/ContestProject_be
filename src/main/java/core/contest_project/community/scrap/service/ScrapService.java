package core.contest_project.community.scrap.service;

import core.contest_project.community.scrap.ScrapStatus;
import core.contest_project.user.service.data.UserDomain;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class ScrapService {
    private final ScrapCreator scrapCreator;
    private final ScrapReader scrapReader;
    private final ScrapDeleter scrapDeleter;


    public ScrapStatus flip(Long commentId, UserDomain loginUSer) {
        if(scrapReader.isLiked(commentId, loginUSer.getId())){
            scrapDeleter.remove(commentId, loginUSer.getId());
            return ScrapStatus.UNSCRAP;
        }

        scrapCreator.create(commentId, loginUSer.getId());
        return ScrapStatus.SCRAP;
    }


}
