package core.contest_project.community.post.repository;

import core.contest_project.common.error.post.PostErrorResult;
import core.contest_project.common.error.post.PostException;
import core.contest_project.contest.entity.Contest;
import core.contest_project.contest.repository.ContestRepository;
import core.contest_project.community.post.entity.Post;
import core.contest_project.community.post.service.PostRepository;
import core.contest_project.community.post.service.data.*;
import core.contest_project.user.entity.User;
import core.contest_project.user.repository.UserJpaRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;


@Repository
@RequiredArgsConstructor
@Slf4j
public class PostRepositoryImpl implements PostRepository {
    private final PostJpaRepository postJpaRepository;
    private final UserJpaRepository userJpaRepository;
    private final ContestRepository contestRepository;

    @Override
    public Long save(PostInfo postInfo, Long userId, String thumbnailUrl) {
        User writer = userJpaRepository.getReferenceById(userId);

        Post post = Post.builder()
                .writer((writer))
                .title(postInfo.title())
                .contestTitle(postInfo.contestTitle())
                .content(postInfo.content())
                .thumbnailUrl(thumbnailUrl)
                .viewCount(0L)
                .likeCount(0L)
                .createAt(LocalDateTime.now())
                .nextAnonymousSeq(1L)
                .build();

        return postJpaRepository.save(post).getId();
    }

    @Override
    public Long save(PostInfo postInfo, Long userId, String thumbnailUrl, Long contestId) {
        Contest contest = contestRepository.getReferenceById(contestId);
        User writer = userJpaRepository.getReferenceById(userId);

        Post post = Post.builder()
                .writer((writer))
                .title(postInfo.title())
                .contestTitle(postInfo.contestTitle())
                .content(postInfo.content())
                .thumbnailUrl(thumbnailUrl)
                .viewCount(0L)
                .likeCount(0L)
                .createAt(LocalDateTime.now())
                .nextAnonymousSeq(1L)
                .contest(contest)
                .build();

        return postJpaRepository.save(post).getId();

    }

    @Override
    public PostDomain findByPostIdJoinWriter(Long id) {
      return postJpaRepository.findByPostIdJoinWriter(id)
              .orElseThrow(() -> new PostException(PostErrorResult.POST_NOT_FOUND)).toPostDomain();
    }


    @Override
    public PostUpdateDomain findByPostIdJoinWriterAndFilesForUpdate(Long postId) {
        return postJpaRepository.findByPostIdJoinWriterAndFiles(postId)
                .orElseThrow(() -> new PostException(PostErrorResult.POST_NOT_FOUND)).toUpdateDomain();
    }

    @Override
    public Slice<PostPreviewDomain> findSlice(Pageable pageable) {
        return postJpaRepository.findSliceBy(pageable).map(Post::toPostPreviewDomain);
    }

    @Override
    public Page<PostPreviewDomain> findPage(Pageable pageable) {
        log.info("[PostRepositoryImpl][findPage]");
        return postJpaRepository.findPageBy(pageable).map(Post::toPostPreviewDomain);
    }

    @Override
    public Slice<PostPreviewDomain> findScrapedPostsByUserId(Long userId, Pageable pageable) {
        Slice<Post> posts = postJpaRepository.findScrapedPostsByUserId(userId, pageable);
        return posts.map(Post::toPostPreviewDomain);
    }

    @Override
    public Slice<PostPreviewDomain> findPostsByUserId(Long userId, Pageable pageable) {
        Slice<Post> posts = postJpaRepository.findPostsByUserId(userId, pageable);
        return posts.map(Post::toPostPreviewDomain);
    }

    @Override
    public Slice<PostPreviewDomain> findPopularPosts(LocalDateTime onWeekAgo, Pageable pageable) {
        return postJpaRepository.findPopularPosts(onWeekAgo,pageable).map(Post::toPostPreviewDomain);
    }

    @Override
    public Slice<PostPreviewDomain> findPopularTips(LocalDateTime onWeekAgo, Pageable pageable, Long contestId) {
        return postJpaRepository.findPopularTips(onWeekAgo, pageable, contestId).map(Post::toPostPreviewDomain);
    }

    @Override
    public Slice<PostPreviewDomain> findRecentTips(Pageable pageable, Long contestId) {
        return postJpaRepository.findRecentTips(pageable, contestId).map(Post::toPostPreviewDomain);
    }

    @Override
    public Slice<PostActivityDomain> findPostsByTeamMemberCode(String teamMemberCode, Pageable pageable) {
        Slice<PostActivityDomain> posts = postJpaRepository.findPostsByTeamMemberCode(teamMemberCode, pageable)
                .map((post)->new PostActivityDomain(post.getId(), post.getTitle(), post.getContent()));
        return posts;
    }

    @Override
    public void deleteByPostId(Long postId) {
        postJpaRepository.deletePostById(postId);
    }

    @Override
    public boolean existsById(Long postId) {
        return postJpaRepository.existsById(postId);
    }

    @Override
    public void update(Long postId, PostInfo info, String thumbnailUrl) {
        Post post = postJpaRepository.findById(postId)
                .orElseThrow(() -> new PostException(PostErrorResult.POST_NOT_FOUND));

        post.update(info, thumbnailUrl);
    }

    @Override
    public void updateViewCount(Long postId) {
        postJpaRepository.updateViewCount(postId);
    }

    @Override
    public void increaseLikeCount(Long postId) {
        postJpaRepository.increaseLikeCount(postId);
    }

    @Override
    public void decreaseLikeCount(Long postId) {
        postJpaRepository.decreaseLikeCount(postId);
    }

    @Override
    public void updateNextAnonymousSeq(Long postId) {
        postJpaRepository.updateNextAnonymousSeq(postId);
    }

}
