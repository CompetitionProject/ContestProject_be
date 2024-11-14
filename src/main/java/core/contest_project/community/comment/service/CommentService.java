package core.contest_project.community.comment.service;

import core.contest_project.community.comment.service.date.CommentActivityDomain;
import core.contest_project.community.comment.service.date.CommentDomain;
import core.contest_project.community.comment.service.date.MyCommentDomain;
import core.contest_project.community.comment.service.date.ParentCommentDomain;
import core.contest_project.community.post.service.PostRepository;
import core.contest_project.community.post.service.data.PostDomain;
import core.contest_project.user.service.data.UserDomain;
import core.contest_project.user.service.UserValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class CommentService {
    private final CommentCreator commentCreator;
    private final CommentReader commentReader;
    private final CommentDeleter commentDeleter;
    private final PostRepository postRepository;
    private final UserValidator userValidator;


    public Long write(Long postId, UserDomain writer, String content, boolean isReply, Long parentId, Boolean isAnonymous) {
        log.info("[CommentService][write]");
        // 유저
        // 나중에 수정

        // 게시글.
        PostDomain postDomain = postRepository.findByPostIdJoinWriter(postId);

        Long id = commentCreator.write(writer, postDomain, content, isReply, parentId, isAnonymous);
        log.info("id: ={}", id);
        return id;
    }


    public List<ParentCommentDomain> getParentsAndChildren(Long postId, UserDomain loginUser) {
        return commentReader.getParentsAndChildren(postId, loginUser);
    }

    public Slice<MyCommentDomain> getMyComments(UserDomain user, Integer page){
        return commentReader.getMyComments(user, page);
    }

    public Slice<CommentActivityDomain> getCommentsByTeamMemberCode(String teamMemberCode, Integer page){
        return commentReader.getCommentsByTeamMemberCode(teamMemberCode, page);
    }

    /**
     *
     * @param commentId
     * @param loginUser
     *
     *
     * @apiNote
     *
     * if) 댓글이 없는 경우
     * : 예외 발생
     *
     *  if) 로그인 유저 != 작성자
     *  : 예외 발생
     *  else if) 로그인 유저 == 작성자
     *      if) 댓글인 경우
     *          if) 대댓글이 있는 경우
     *          : isDeleted=true
     *          else if) 대댓글이 없는 경우
     *              if) 좋아요가 있는 경우
     *              : 좋아요 삭제 <- cascade 좋아요도 영속 상태?
     *              else if) 좋아요가 없는 경우
     *          : 댓글 삭제
     *      else if) 대댓글인 경우
     *          if) 좋아요가 있는 경우
     *          : 좋아요 삭제
     *          else if) 좋아요가 없는 경우
     *      : 댓글 삭제
     *
     */

    public void delete(Long commentId, UserDomain loginUser) {
        log.info("[CommentService][delete]");
        // 댓글 조회.

        // 댓글 없었으면 여기서 예외 터짐?
        CommentDomain commentDomain = commentReader.getComment(commentId);

        // loginUser == writer?
        userValidator.isSame(loginUser.getId(), commentDomain.getInfo().getWriterDomain());

        commentDeleter.delete(commentDomain);
    }


}
