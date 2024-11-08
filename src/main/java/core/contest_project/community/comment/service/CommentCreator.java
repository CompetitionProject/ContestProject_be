package core.contest_project.community.comment.service;

import core.contest_project.community.comment.service.date.CommentInfo;
import core.contest_project.community.post.service.data.PostDomain;
import core.contest_project.community.post.service.PostUpdater;
import core.contest_project.community.user.service.data.UserDomain;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
@RequiredArgsConstructor
@Slf4j
public class CommentCreator {
    private final CommentRepository commentRepository;
    private final CommentReader commentReader;
    private final PostUpdater postUpdater;
    /**
     *
     * @param postDomain
     * @param content
     * @param isReply
     * @param parentId
     * @param isAnonymous
     * @return
     *
     * @apiNote
     *
     * 1. writer 가 작성? <- 무조건 실명임.
     *  1-1) 과거 익명으로 한 번이라도 쓴 적 있음.
     *      1-1-1) 현재 익명이면
     *          기존 익명 번호 사용.
     *      1-1-2) 현재 익명 x
     *          그냥 씀
     *  1-2) 안 씀
     *      1-2-1) 현재 익명이면
     *          새로운 익명 번호 할당 여기서 post update 해야겠는데? 변경 감지로 될까?
     *      1-2-2) 현재 익명 x
     *          그냥 씀.
     *
     */
    public Long write(UserDomain writer, PostDomain postDomain, String content, boolean isReply, Long parentId, Boolean isAnonymous) {
        log.info("[CommentCreator][write]");

        // 게시글 작성자가 댓글 작성?
        if(Objects.equals(writer.getId(), postDomain.getWriter().getId())){isAnonymous=false;}

        Long anonymousNumber = isAnonymous ? getAnonymousNumber(writer, postDomain) : null;

        CommentInfo commentInfo = CommentInfo.builder()
                .writerDomain(writer)
                .content(content)
                .isReply(isReply)
                .parentId(parentId)
                .isAnonymous(isAnonymous)
                .anonymousNumber(anonymousNumber)
                .build();

        return commentRepository.save(commentInfo, postDomain);


    }

    private Long getAnonymousNumber(UserDomain writer, PostDomain postDomain) {
        Long anonymousNumber= commentReader.getAnonymousNumber(postDomain.getId(), writer.getId());
        if(anonymousNumber==null){
            // 새롭게 할당.
            anonymousNumber= postDomain.getNextAnonymousSeq();
            // update query 날려주기.
            postUpdater.increaseNextAnonymousSeq(postDomain.getId());
        }

        return anonymousNumber;
    }
}
