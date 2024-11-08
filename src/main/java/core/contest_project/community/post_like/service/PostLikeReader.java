package core.contest_project.community.post_like.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class PostLikeReader {
    private final PostLikeRepository postLikeRepository;

    public boolean isLiked(Long postId, Long userId) {
        log.info("[PostLikeReader][isLiked]");
        return postLikeRepository.isLiked(postId, userId);

    }


    public Long count(Long postId) {
        return postLikeRepository.count(postId);
    }
}
