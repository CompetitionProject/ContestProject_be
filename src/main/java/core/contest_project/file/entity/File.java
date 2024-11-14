package core.contest_project.file.entity;

import core.contest_project.file.FileLocation;
import core.contest_project.file.FileType;
import core.contest_project.file.service.data.FileDomain;
import core.contest_project.file.service.data.FileInfo;
import core.contest_project.community.post.entity.Post;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

import static jakarta.persistence.FetchType.LAZY;
import static jakarta.persistence.GenerationType.IDENTITY;
import static lombok.AccessLevel.PROTECTED;


@Entity
@NoArgsConstructor(access=PROTECTED)
@Builder
@AllArgsConstructor
@Getter
public class File {

    @Id @GeneratedValue(strategy = IDENTITY)
    @Column(name="file_id")
    private Long id;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name="post_id")
    private Post post;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name="contest_id")
    private Contest contest;

    @Enumerated(EnumType.STRING)
    private FileLocation location;

    private String url;
    private String uploadName;
    @Enumerated(EnumType.STRING)
    private FileType fileType;
    private LocalDateTime createAt;


    public FileDomain toDomain(){
        return FileDomain.builder()
                .id(id)
                .info(FileInfo.builder()
                        .storeFileName(url)
                        .uploadFileName(uploadName)
                        .fileType(fileType)
                        .location(location)
                        .build())
                .build();
    }


    public void setPost(Post post) {
        this.post = post;
    }

    public void setContest(Contest contest) {
        this.contest = contest;
    }

}
