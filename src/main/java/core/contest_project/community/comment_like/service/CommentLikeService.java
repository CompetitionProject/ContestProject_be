package core.contest_project.community.comment_like.service;

import core.contest_project.community.comment_like.CommentLikeStatus;
import core.contest_project.user.service.data.UserDomain;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class CommentLikeService {
    private final CommentLikeCreator commentLikeCreator;
    private final CommentLikeReader commentLikeReader;
    private final CommentLikeDeleter commentLikeDeleter;

    public CommentLikeStatus flip(Long commentId, UserDomain loginUSer) {
        if(commentLikeReader.isLiked(commentId, loginUSer.getId())){
            commentLikeDeleter.remove(commentId, loginUSer.getId());
            return CommentLikeStatus.UNLIKE;
        }

        commentLikeCreator.create(commentId, loginUSer.getId());
        return CommentLikeStatus.LIKE;
    }

}
