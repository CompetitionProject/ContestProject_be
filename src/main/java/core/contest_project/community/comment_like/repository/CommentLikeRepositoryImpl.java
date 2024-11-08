package core.contest_project.community.comment_like.repository;

import core.contest_project.community.comment.entity.Comment;
import core.contest_project.community.comment.repository.CommentJpaRepository;
import core.contest_project.community.comment_like.entity.CommentLike;
import core.contest_project.community.comment_like.service.CommentLikeDomain;
import core.contest_project.community.comment_like.service.CommentLikeRepository;
import core.contest_project.community.user.entity.User;
import core.contest_project.community.user.repository.UserJpaRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
@Slf4j
public class CommentLikeRepositoryImpl implements CommentLikeRepository {
    private final CommentLikeJpaRepository commentLikeJpaRepository;
    private final CommentJpaRepository commentJpaRepository;
    private final UserJpaRepository userJpaRepository;



    @Override
    public boolean isLiked(Long commentId , Long userId) {
       return commentLikeJpaRepository.existsByComment_IdAndUser_Id(commentId, userId);
    }

    @Override
    public boolean isLiked(Long commentId) {
        return commentLikeJpaRepository.existsByComment_Id(commentId);
    }

    @Override
    public Long count(Long commentId) {
        return commentLikeJpaRepository.countByComment_Id(commentId);
    }


    @Override
    public void delete(Long commentId, Long userId) {
        commentLikeJpaRepository.deleteByComment_IdAndUser_Id(commentId, userId);
    }

    @Override
    public void deleteAll(Long commentId) {
        commentLikeJpaRepository.deleteAllByComment_Id(commentId);
    }

    @Override
    public void deleteAllByUserId(Long userId) {
        commentLikeJpaRepository.deleteAllByUser_Id(userId);
    }

    @Override
    public void deleteAll(List<Long> commentIds) {
        commentLikeJpaRepository.deleteAllByCommentIds(commentIds);
    }

    @Override
    public void save(CommentLikeDomain commentLikeDomain) {
       /* User findUser = userJpaRepository.findById(commentLikeDomain.userId())
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND, "user not found"));
        Comment findComment = commentJpaRepository.findById(commentLikeDomain.commentId())
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND, "comment not found"));*/

        User findUser = userJpaRepository.getReferenceById(commentLikeDomain.userId());
        Comment findComment = commentJpaRepository.getReferenceById(commentLikeDomain.commentId());


        CommentLike like = CommentLike.builder()
                .user(findUser)
                .comment(findComment)
                .build();
        commentLikeJpaRepository.save(like);
    }

    @Override
    public void save(Long commentId, Long userId) {
        User findUser = userJpaRepository.getReferenceById(userId);
        Comment findComment = commentJpaRepository.getReferenceById(commentId);


        CommentLike like = CommentLike.builder()
                .user(findUser)
                .comment(findComment)
                .build();
        commentLikeJpaRepository.save(like);
    }
}
