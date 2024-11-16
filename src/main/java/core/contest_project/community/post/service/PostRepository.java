package core.contest_project.community.post.service;

import core.contest_project.community.post.service.data.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

import java.time.LocalDateTime;

public interface PostRepository {
    Long save(PostInfo postInfo, Long userId, String thumbnailUrl);
    Long save(PostInfo postInfo, Long userId, String thumbnailUrl, Long contestId);

    PostDomain findByPostIdJoinWriter(Long postId);
    PostUpdateDomain findByPostIdJoinWriterAndFilesForUpdate(Long postId);
    Slice<PostPreviewDomain> findSlice(Pageable pageable);
    Page<PostPreviewDomain> findPage(Pageable pageable);
    Slice<PostPreviewDomain> findScrapedPostsByUserId(Long userId, Pageable pageable);
    Slice<PostPreviewDomain> findPostsByUserId(Long userId, Pageable pageable);
    Slice<PostPreviewDomain> findPopularPosts(LocalDateTime onWeekAgo, Pageable pageable);
    Slice<PostPreviewDomain> findPopularTips(LocalDateTime onWeekAgo, Pageable pageable, Long contestId);
    Slice<PostPreviewDomain> findRecentTips(Pageable pageable, Long contestId);
    Slice<PostActivityDomain>findPostsByTeamMemberCode(String teamMemberCode, Pageable pageable);

    void update(Long postId, PostInfo info, String thumbnailUrl);
    void updateViewCount(Long postId);
    void increaseLikeCount(Long postId);
    void decreaseLikeCount(Long postId);
    void updateNextAnonymousSeq(Long postId);

    void deleteByPostId(Long postId);
}
