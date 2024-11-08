package core.contest_project.community.comment.service;

import core.contest_project.community.comment.service.date.CommentDomain;
import core.contest_project.community.comment_like.service.CommentLikeDeleter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class CommentDeleter {
    private final CommentRepository commentRepository;
    private final CommentUpdater commentUpdater;
    private final CommentLikeDeleter commentLikeDeleter;

    public void delete(CommentDomain commentDomain){
        Long commentId = commentDomain.getId();

        // 좋아요 전부 삭제.
        commentLikeDeleter.removeAll(commentId);

        if(!commentDomain.getInfo().isReply()
                && hasChild(commentId)){ // 댓글이고 대댓글이 있으면
            // isDeleted=true
            commentUpdater.updateCommentStatusToDeleted(commentId);
        }
        else{ // 1) 댓글이고 대댓글이 없는 경우 2) 대댓글인 경우
            // 댓글 삭제.
            commentRepository.deleteById(commentId);
        }
    }

    public void removeAll(Long postId) {
        // 좋아요 지우기.
        List<CommentDomain> comments = commentRepository.findAllByPostId(postId);
        List<Long> commentIds = getCommentIds(comments);

        commentLikeDeleter.removeAll(commentIds);

        commentRepository.deleteAllByPostId(postId);
    }

    private List<Long> getCommentIds(List<CommentDomain> commentDomains){
        List<Long> commentIds = new ArrayList<>();
        for(CommentDomain commentDomain : commentDomains){
            commentIds.add(commentDomain.getId());
        }
        return commentIds;
    }

    private boolean hasChild(Long parentId){
        return commentRepository.existsByParentId(parentId);

    }
}
