package core.contest_project.community.post_like.service;

import core.contest_project.community.post.service.PostUpdater;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class PostLikeCreator {
    private final PostLikeRepository postLikeRepository;
    private final PostUpdater postUpdater;

    public void create(Long postId, Long userId) {
        postUpdater.increaseLikeCount(postId);
        postLikeRepository.save(postId, userId);
    }
}
