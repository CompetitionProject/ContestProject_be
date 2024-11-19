package core.contest_project.community.scrap.service;

import core.contest_project.common.error.post.PostErrorResult;
import core.contest_project.common.error.post.PostException;
import core.contest_project.community.post.service.PostReader;
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
    private final PostReader postReader;


    public ScrapStatus flip(Long postId, UserDomain loginUSer) {
        if(!postReader.existsById(postId)){
            log.info("post is not found");
            throw new PostException(PostErrorResult.POST_NOT_FOUND);
        }
        if(scrapReader.isLiked(postId, loginUSer.getId())){
            scrapDeleter.remove(postId, loginUSer.getId());
            return ScrapStatus.UNSCRAP;
        }

        scrapCreator.create(postId, loginUSer.getId());
        return ScrapStatus.SCRAP;
    }


}
