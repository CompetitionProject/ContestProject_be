package core.contest_project.community.post_like.repository;

import core.contest_project.community.post.entity.Post;
import core.contest_project.community.post.repository.PostJpaRepository;
import core.contest_project.community.post_like.entity.PostLike;
import core.contest_project.community.post_like.service.PostLikeDomain;
import core.contest_project.community.post_like.service.PostLikeRepository;
import core.contest_project.community.user.entity.User;
import core.contest_project.community.user.repository.UserJpaRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
@Slf4j
public class PostLikeRepositoryImpl implements PostLikeRepository {
    private final PostLikeJpaRepository postLikeJpaRepository;
    private final PostJpaRepository postJpaRepository;
    private final UserJpaRepository userJpaRepository;

    @Override
    public boolean isLiked(Long postId, Long userId) {
        return postLikeJpaRepository.existsByPost_IdAndUser_Id(postId, userId);
    }

    @Override
    public Long count(Long postId) {
        return postLikeJpaRepository.countByPost_Id(postId);
    }

    @Override
    public void delete(PostLikeDomain postLikeDomain) {
        log.info("[PostLikeRepositoryImpl][delete]");
        postLikeJpaRepository.deleteByPost_IdAndUser_Id(postLikeDomain.postId(), postLikeDomain.userId());
    }

    @Override
    public void delete(Long postId, Long userId) {
        postLikeJpaRepository.deleteByPost_IdAndUser_Id(postId, userId);
    }

    @Override
    public void deleteAll(Long postId) {
        postLikeJpaRepository.deleteAllByPostId(postId);
    }

    @Override
    public void deleteALlByUserId(Long userId) {
        postLikeJpaRepository.deleteAllByUser_Id(userId);
    }

    /**
     *
     * @param postLikeDomain
     *
     * @apiNote
     * 연관관계 때문에 조회하고 insert 하는 거 같은데
     * 이거 고민좀 해봐야겠다.
     * 프록시 쓰라고?? -> getOne?
     */

    @Override
    public void save(PostLikeDomain postLikeDomain) {
        log.info("[PostLikeRepositoryImpl][save]");
        /*User findUser = userJpaRepository.findById(postLikeDomain.userId())
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND, "user not found"));
        Post findPost = postJpaRepository.findById(postLikeDomain.postId())
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND, "post not found"));*/

        User findUser = userJpaRepository.getReferenceById(postLikeDomain.userId());
        Post findPost = postJpaRepository.getReferenceById(postLikeDomain.postId());


        PostLike like = PostLike.builder()
                .user(findUser)
                .post(findPost)
                .build();
        postLikeJpaRepository.save(like);
    }

    @Override
    public void save(Long postId, Long userId) {
        User findUser = userJpaRepository.getReferenceById(userId);
        Post findPost = postJpaRepository.getReferenceById(postId);


        PostLike like = PostLike.builder()
                .user(findUser)
                .post(findPost)
                .build();
        postLikeJpaRepository.save(like);
    }
}
