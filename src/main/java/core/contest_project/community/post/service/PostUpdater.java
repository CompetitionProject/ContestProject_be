package core.contest_project.community.post.service;

import core.contest_project.community.post.service.data.PostInfo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class PostUpdater {
    private final PostRepository postRepository;

    public void update(Long postId, PostInfo info){
        postRepository.update(postId, info);
    }



    /**
     * @param postId 이거 일반 게시글이랑 인기 게시글 분리?
     */
    public void increaseViewCount(Long postId) {
        postRepository.updateViewCount(postId);
    }

    public void increaseLikeCount(Long postId) {
        postRepository.increaseLikeCount(postId);
    }

    /**
     * 이거 음수이면 어떻게 하냐?
     * @param postId
     */
    public void decreaseLikeCount(Long postId) {
        postRepository.decreaseLikeCount(postId);
    }

    public void increaseNextAnonymousSeq(Long postId) {
        postRepository.updateNextAnonymousSeq(postId);
    }


}
