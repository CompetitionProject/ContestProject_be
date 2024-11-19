package core.contest_project.community.post_like.service;

import core.contest_project.common.error.post.PostErrorResult;
import core.contest_project.common.error.post.PostException;
import core.contest_project.community.post.service.PostReader;
import core.contest_project.community.post_like.PostLikeStatus;
import core.contest_project.user.service.data.UserDomain;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class PostLikeService {
    private final PostLikeCreator postLikeCreator;
    private final PostLikeReader postLikeReader;
    private final PostLikeDeleter postLikeDeleter;
    private final PostReader postReader;

    public PostLikeStatus flip(Long postId, UserDomain loginUSer) {
        if(!postReader.existsById(postId)){
            log.info("post is not found");
            throw new PostException(PostErrorResult.POST_NOT_FOUND);
        }
        if(postLikeReader.isLiked(postId, loginUSer.getId())){
            postLikeDeleter.remove(postId, loginUSer.getId());
            return PostLikeStatus.UNLIKE;
        }

        postLikeCreator.create(postId, loginUSer.getId());
        return PostLikeStatus.LIKE;
    }


}
