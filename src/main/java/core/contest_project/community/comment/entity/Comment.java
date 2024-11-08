package core.contest_project.community.comment.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import core.contest_project.community.comment.service.date.CommentDomain;
import core.contest_project.community.comment.service.date.CommentInfo;
import core.contest_project.community.comment.service.date.MyCommentDomain;
import core.contest_project.community.comment_like.entity.CommentLike;
import core.contest_project.community.comment_like.service.CommentLikeDomain;
import core.contest_project.community.file.service.data.FileDomain;
import core.contest_project.community.post.entity.Post;
import core.contest_project.community.post.service.data.PostPreviewDomain;
import core.contest_project.community.user.entity.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;
import java.util.List;

import static jakarta.persistence.FetchType.LAZY;
import static jakarta.persistence.GenerationType.IDENTITY;
import static lombok.AccessLevel.PROTECTED;


/**
 * 나중에 알림 추가할 것.
 * 이거 postId랑 createAt으로 인덱스 하면 빠를라나?
 */

@Entity
@Getter
@NoArgsConstructor(access=PROTECTED)
@Builder
@AllArgsConstructor
@Slf4j
public class Comment {
    @Id @GeneratedValue(strategy = IDENTITY)
    @Column(name="comment_id")
    private Long id;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name="post_id")
    private Post post;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name="writer_id")
    private User writer;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name="parent_id")
    private Comment parent;



    @OneToMany(mappedBy="comment", fetch=LAZY)
    @JsonIgnore
    private List<CommentLike> likes;

    private String content;

    private LocalDateTime createAt;

    boolean isAnonymous;

    private Long anonymousNumber;

    boolean isDeleted;


    public CommentDomain toCommentDomain(){
        CommentInfo info;

        if(getParent()==null){
            // 댓글이면
            info= CommentInfo.builder()
                    .writerDomain(getWriter().toDomain())
                    .content(content)
                    .isReply(false)
                    .parentId(null)
                    .isAnonymous(isAnonymous)
                    .anonymousNumber(anonymousNumber)
                    .build();
        }
        else{
            // 대댓글이면
            info=CommentInfo.builder()
                    .writerDomain(getWriter().toDomain())
                    .content(content)
                    .isReply(true)
                    .parentId(getParent().id)
                    .isAnonymous(isAnonymous)
                    .anonymousNumber(anonymousNumber)
                    .build();
        }

        List<CommentLikeDomain> likeDomains=getLikes().stream()
                .map(CommentLike::toDomain)
                .toList();

        return CommentDomain.builder()
                .id(id)
                .info(info)
                .createAt(createAt)
                .isDelete(isDeleted)
                .likes(likeDomains)
                .build();
    }

    public MyCommentDomain toMyCommentDomain(){
        Long likeCount= (long) likes.size();
        PostPreviewDomain postDomain = getPost().toPostPreviewDomain(); // 여기서 comments 쿼리 하나 더 나감.
        FileDomain thumbnail = postDomain.getThumbnail();


        return MyCommentDomain.builder()
                .id(id)
                .content(content)
                .likeCount(likeCount)
                .postId(postDomain.getPostId())
                .postTitle(postDomain.getInfo().title())
                .postWriterNickname(postDomain.getWriterNickname())
                .postViewCount(postDomain.getViewCount())
                .postCreatedAt(postDomain.getCreateAt())
                .thumbnail(thumbnail)
                .build();
    }

}
