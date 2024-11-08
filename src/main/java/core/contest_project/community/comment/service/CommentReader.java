package core.contest_project.community.comment.service;

import core.contest_project.community.comment.service.date.*;
import core.contest_project.community.comment_like.service.CommentLikeDomain;
import core.contest_project.community.file.service.data.FileDomain;
import core.contest_project.community.file.service.storage.FileManager;
import core.contest_project.community.user.service.data.UserDomain;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

@Component
@RequiredArgsConstructor
@Slf4j
public class CommentReader {
    private final CommentRepository commentRepository;
    private final FileManager fileManager;

    public CommentDomain getComment(Long commentId){
        return commentRepository.findByCommentId(commentId);
    }

    public List<ParentCommentDomain> getParentsAndChildren(Long postId, UserDomain loginUser){
        List<CommentDomain> comments = commentRepository.findAllByPostId(postId);
        handleComment(comments, loginUser);
        return associateChildWithParent(comments);
    }

    public Slice<MyCommentDomain> getMyComments(UserDomain loginUser, Integer page){
        Pageable pageable = PageRequest.of(page, 6, Sort.by(Sort.Direction.DESC, "createAt"));
        Slice<MyCommentDomain> comments = commentRepository.findAllByUserId(loginUser.getId(), pageable);
        List<MyCommentDomain> contents = comments.getContent();
        setThumbnailUrls(contents);
        return comments;

    }

    public Slice<CommentActivityDomain> getCommentsByTeamMemberCode(String teamMemberCode, Integer page){
        Pageable pageable=PageRequest.of(page, 5, Sort.by(Sort.Direction.DESC, "createAt"));

        return commentRepository.findCommentsByTeamMemberCode(teamMemberCode,pageable);
    }

    public Long getAnonymousNumber(Long postId, Long writerId){
        return commentRepository.findAnonymousNumber(postId, writerId);
    }


    private void setThumbnailUrls(List<MyCommentDomain> comments) {
        for (MyCommentDomain comment : comments) {
            FileDomain thumbnail = comment.getThumbnail();
            if (thumbnail != null) {
                String url = fileManager.getUrl(thumbnail.getInfo().getStoreFileName());
                thumbnail.setUrl(url);
            }
        }
    }

    private void handleComment(List<CommentDomain> comments, UserDomain loginUser) {
        Long loginUserId = loginUser.getId();

        for (CommentDomain comment : comments) {
            // 좋아요
            boolean isLiked = false;
            for (CommentLikeDomain commentLikeDomain : comment.getLikes()) {
                if (Objects.equals(commentLikeDomain.userId(), loginUserId)) {
                    isLiked = true;
                    break;
                }
            }

            comment.setLiked(isLiked);
            // isWriter
            log.info("writerId= {}", comment.getInfo().getWriterDomain().getId());
            log.info("loginUserId= {}", loginUserId);
            if(Objects.equals(comment.getInfo().getWriterDomain().getId(), loginUserId)){
                log.info("같음");
                comment.setWriter(true);
            }
            // anonymous: 여기 profileUrl 도 처리해줘야 할듯?
            if (comment.getInfo().isAnonymous()) {
                comment.anonymize();
            }

            // delete 처리도 해야 하나?
        }
    }

    private List<ParentCommentDomain> associateChildWithParent(List<CommentDomain> comments) {
        List<ParentCommentDomain> parents = new ArrayList<>();
        // key: parentId, value: idx
        ConcurrentHashMap<Long, Integer> map = new ConcurrentHashMap<>();

        for (CommentDomain comment : comments) {
            Long commentLikeCount;
            boolean isLiked;

            commentLikeCount= (long) comment.getLikes().size();
            isLiked=comment.isLiked();

            if(!comment.getInfo().isReply()){
                parents.add(ParentCommentDomain.builder()
                        .id(comment.getId())
                        .info(comment.getInfo())
                        .createAt(comment.getCreateAt())
                        .isDeleted(comment.isDelete())
                        .isWriter(comment.isWriter())
                        .commentLikeCount(commentLikeCount)
                        .isLiked(isLiked)
                        .replies(new ArrayList<>())
                        .build());
                Integer idx=parents.size()-1;

                map.put(comment.getId(), idx);
            }
            else{
                ChildCommentDomain child = ChildCommentDomain.builder()
                        .id(comment.getId())
                        .info(comment.getInfo())
                        .createAt(comment.getCreateAt())
                        .isDeleted(comment.isDelete())
                        .isWriter(comment.isWriter())
                        .commentLikeCount(commentLikeCount)
                        .isLiked(isLiked)
                        .build();

                Integer idx=map.get(child.getInfo().getParentId());

                parents.get(idx).associateChild(child);
            }
        }
        return parents;
    }

}
