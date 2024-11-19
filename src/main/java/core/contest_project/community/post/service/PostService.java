package core.contest_project.community.post.service;

import core.contest_project.file.FileLocation;
import core.contest_project.file.FileType;
import core.contest_project.file.FileUtil;
import core.contest_project.file.entity.File;
import core.contest_project.file.service.FileRequest;
import core.contest_project.file.service.FileResponse;
import core.contest_project.file.service.db.FileCreator;
import core.contest_project.file.service.db.FileReader;
import core.contest_project.file.service.db.FileUpdater;
import core.contest_project.community.post.service.data.*;
import core.contest_project.user.service.UserValidator;
import core.contest_project.user.service.data.UserDomain;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class PostService {
    private final PostCreator postCreator;
    private final PostReader postReader;
    private final PostUpdater postUpdater;
    private final PostDeleter postDeleter;
    private final PostRepository postRepository;
    private final UserValidator userValidator;
    private final FileCreator fileCreator;
    private final FileUpdater fileUpdater;
    private final FileReader fileReader;

    public Long write(PostInfo post, List<FileRequest> requestFiles, UserDomain writer){
        // FileRequest -> FileEntity
        List<File> files = FileUtil.toEntity(requestFiles, FileLocation.POST);

        // 썸네일.
        String thumbnailUrl = getThumbnailUrl(files);

        // 게시글
        Long postId = postCreator.create(post, writer, thumbnailUrl);
        // 파일
        fileCreator.saveAll(postId, files);

        return postId;
    }

    public Long createTip(PostInfo post, List<FileRequest> requestFiles, UserDomain writer, Long contestId){
        // FileRequest -> FileEntity
        List<File> files = FileUtil.toEntity(requestFiles, FileLocation.POST);

        // 썸네일.
        String thumbnailUrl = getThumbnailUrl(files);

        // 게시글
        Long postId = postCreator.create(post, writer, thumbnailUrl, contestId);
        // 파일
        fileCreator.saveAll(postId, files);

        return postId;
    }


    public PostDomain getPost(Long postId, UserDomain loginUser) {
        PostDomain post = postReader.getPost(postId, loginUser);
        List<File> findFiles = fileReader.getFiles(postId, FileLocation.POST);
        List<FileResponse> files = FileUtil.toResponse(findFiles);
        post.setFiles(files);

        return post;
    }

    public Slice<PostPreviewDomain> getPopularPosts(Integer page, Integer size){
        return postReader.getPopularPosts(page, size);
    }

    public Slice<PostPreviewDomain> getPopularTips(Integer page, Long contestId){
        return postReader.getPopularTips(page, 4, contestId);
    }

    public Slice<PostPreviewDomain> getRecentTips(Integer page, Long contestId){
        return postReader.getRecentTips(page, 4, contestId);
    }

    public Page<PostPreviewDomain> getPosts(Integer page, PostSortType sort) {
        return postReader.getPosts(page, sort);
    }

    public Slice<PostActivityDomain> getPostsByTeamMemberCode(String teamMemberCode, Integer page) {
        return postReader.getPostsByTeamMemberCode(teamMemberCode, page);
    }

    public void update(Long postId, PostInfo postInfo, UserDomain loginUser, List<FileRequest> fileForUpdate) {
        log.info("[PostService][update]");
        PostUpdateDomain post = postRepository.findByPostIdJoinWriterAndFilesForUpdate(postId);
        userValidator.isSame(post.getWriter().getId(), loginUser.getId());

        List<File> fileEntityForUpdate = FileUtil.toEntity(fileForUpdate, FileLocation.POST);
        String thumbnailUrl = getThumbnailUrl(fileEntityForUpdate);

        // 게시글 수정
        postUpdater.update(postId, postInfo, thumbnailUrl);

        // 파일 수정

        fileUpdater.update(postId, FileLocation.POST, fileEntityForUpdate);


    }

    public void delete(Long postId, UserDomain loginUser) {
        PostDomain postDomain = postRepository.findByPostIdJoinWriter(postId);
        userValidator.isSame(postDomain.getWriter().getId(), loginUser.getId());
        postDeleter.delete(postDomain);
    }


    private String getThumbnailUrl(List<File> files){
        for (File file : files) {
            if(file.getFileType()== FileType.IMAGE){
                return file.getUrl();
            }
        }
        return null;
    }

}
