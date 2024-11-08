package core.contest_project.community.post.service;

import core.contest_project.community.file.service.data.FileDomain;
import core.contest_project.community.file.service.storage.FileManager;
import core.contest_project.community.post.service.data.PostActivityDomain;
import core.contest_project.community.post.service.data.PostDomain;
import core.contest_project.community.post.service.data.PostPreviewDomain;
import core.contest_project.community.post.service.data.PostSortType;
import core.contest_project.community.post_like.service.PostLikeReader;
import core.contest_project.community.scrap.service.ScrapReader;
import core.contest_project.community.user.service.data.UserDomain;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

@Component
@RequiredArgsConstructor
@Slf4j
public class PostReader {
    private final PostUpdater postUpdater;
    private final PostRepository postRepository;
    private final PostLikeReader postLikeReader;
    private final ScrapReader scrapReader;
    private final FileManager fileManager;


    public PostDomain getPost(Long postId, UserDomain loginUser) {
        PostDomain postDomain = postRepository.findByPostIdJoinWriter(postId);
        // 조회수 증가. -> 이때 직접 쿼리 -> flush 랑 clear 변경 감지 x?
        postDomain.increaseViewCount();
        postUpdater.increaseViewCount(postDomain.getId());

        // scrap
        Long scrapCount = scrapReader.count(postDomain.getId());
        postDomain.setScrapCount(scrapCount);
        boolean isScraped = scrapReader.isLiked(postDomain.getId(), loginUser.getId());
        postDomain.setIsScraped(isScraped);

        // postLike
        Long postLikeCount = postLikeReader.count(postDomain.getId());
        postDomain.setLikeCount(postLikeCount);
        boolean isLiked = postLikeReader.isLiked(postDomain.getId(), loginUser.getId());
        postDomain.setIsLiked(isLiked);

        // notification

        // file

        // isWriter
        boolean isWriter = Objects.equals(postDomain.getWriter().getId(), loginUser.getId());
        postDomain.setIsWriter(isWriter);

        return postDomain;
    }

    public Slice<PostPreviewDomain> getMostLikedPosts(Integer page) {
        Pageable pageable = PageRequest.of(page, 5, Sort.by(Sort.Direction.DESC, PostSortType.LIKE.getFieldName()));
        Slice<PostPreviewDomain> posts = postRepository.findSlice(pageable);
        setThumbnailUrls(posts.getContent());
        return posts;
    }

    public Slice<PostPreviewDomain> getPopularPosts(Integer page, Integer size) {
        Pageable pageable=PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, PostSortType.LIKE.getFieldName()));
        LocalDateTime oneWeekAgo = LocalDateTime.now().minusWeeks(1);

        // test를 위해 30초 전으로 바꾸자.
        oneWeekAgo = LocalDateTime.now().minusSeconds(30);

        Slice<PostPreviewDomain> popularPosts = postRepository.findPopularPosts(oneWeekAgo, pageable);
        setThumbnailUrls(popularPosts.getContent());
        return popularPosts;

    }

    public Slice<PostActivityDomain> getPostsByTeamMemberCode(String teamMemberCode, Integer page){
        Pageable pageable=PageRequest.of(page, 5, Sort.by(Sort.Direction.DESC, PostSortType.DATE.getFieldName()));

        return postRepository.findPostsByTeamMemberCode(teamMemberCode,pageable);
    }


    public Page<PostPreviewDomain> getPosts(Integer page, PostSortType sort) {
        Pageable pageable = PageRequest.of(page, 6, Sort.by(Sort.Direction.DESC, sort.getFieldName()));

        Page<PostPreviewDomain> posts = postRepository.findPage(pageable);
        setThumbnailUrls(posts.getContent());

        return posts;
    }

    public Slice<PostPreviewDomain> getScrapedPosts(Integer page, String sort, UserDomain loginUser) {
        Pageable pageable = PageRequest.of(page, 6, Sort.by(Sort.Direction.DESC, sort));

        Slice<PostPreviewDomain> posts = postRepository.findScrapedPostsByUserId(loginUser.getId(), pageable);
        setThumbnailUrls(posts.getContent());

        return posts;
    }

    public Slice<PostPreviewDomain> getMyPosts(Integer page, UserDomain loginUser) {
        Pageable pageable = PageRequest.of(page, 6, Sort.by(Sort.Direction.DESC, "createAt"));

        Slice<PostPreviewDomain> posts = postRepository.findPostsByUserId(loginUser.getId(), pageable);
        setThumbnailUrls(posts.getContent());

        return posts;
    }

    private void setThumbnailUrls(List<PostPreviewDomain> posts) {
        for (PostPreviewDomain post : posts) {
            FileDomain thumbnail = post.getThumbnail();
            if (thumbnail != null) {
                String url = fileManager.getUrl(thumbnail.getInfo().getStoreFileName());
                thumbnail.setUrl(url);
            }
        }
    }
}



