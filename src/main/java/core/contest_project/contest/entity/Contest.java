package core.contest_project.contest.entity;

import core.contest_project.common.error.file.FileErrorResult;
import core.contest_project.common.error.file.FileException;
import core.contest_project.contest.dto.request.ContestCreateRequest;
import core.contest_project.contest.dto.request.ContestUpdateRequest;
import core.contest_project.file.FileLocation;
import core.contest_project.file.FileType;
import core.contest_project.file.entity.File;
import core.contest_project.user.entity.User;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Builder(toBuilder = true)
@Entity
public class Contest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "contest_id")
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String content;

    // 본문 이미지
    @Builder.Default
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "contest", orphanRemoval = true)
    private List<File> contentImages = new ArrayList<>();

    // 첨부 파일(지원서)
    @Builder.Default
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "contest", orphanRemoval = true)
    private List<File> attachments = new ArrayList<>();

    //접수일
    @Column(nullable = false)
    private LocalDateTime startDate;

    //마감일
    @Column(nullable = false)
    private LocalDateTime endDate;

    // 조회수
    @Builder.Default
    @Column(name = "view_count")
    private Long viewCount = 0L;

    // 북마크수
    @Builder.Default
    @Column(name = "bookmark_count")
    private Long bookmarkCount = 0L;

    //응모 자격
    @Column(name = "qualification")
    private String qualification;

    //시상 규모
    @Column(name = "award_scale")
    private String awardScale;

    //주최 기관
    @Column(name = "host")
    private String host;

    // 신청 방법
    @Enumerated(EnumType.STRING)
    @Column(name = "application_method")
    private ContestApplicationMethod applicationMethod;

    // 신청 이메일
    @Column(name = "application_email")
    private String applicationEmail;

    // 작성자
    @ManyToOne
    @JoinColumn(name = "writer_id")
    private User writer;

    // 주최 홈페이지 url
    @Column(name = "host_url")
    private String hostUrl;

    // 모집글 분야
    @Builder.Default
    @ElementCollection(targetClass = ContestField.class)
    @CollectionTable(name = "contest_fields", joinColumns = @JoinColumn(name = "contest_id"))
    @Enumerated(EnumType.STRING)
    private List<ContestField> contestFields = new ArrayList<>();

    // 모집글 상태
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ContestStatus contestStatus;

    // 생성일
    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;

    // 수정일
    @UpdateTimestamp
    private LocalDateTime updatedAt;

    @Column
    private LocalDateTime lastStatusUpdate;

    public boolean isExpired() {
        return LocalDateTime.now().isAfter(endDate);
    }

    public void updateStatus() {
        LocalDateTime now = LocalDateTime.now();
        this.contestStatus = calculateStatus(now);
        this.lastStatusUpdate = now;
    }

    private ContestStatus calculateStatus(LocalDateTime now) {
        if (now.isBefore(startDate)) {
            return ContestStatus.NOT_STARTED;
        } else if (now.isAfter(endDate)) {
            return ContestStatus.CLOSED;
        }
        return ContestStatus.IN_PROGRESS;
    }

    public static Contest createContest(ContestCreateRequest request,
                                        User writer) {
        Contest contest = Contest.builder()
                .title(request.title())
                .content(request.content())
                .startDate(request.startDate())
                .endDate(request.endDate())
                .viewCount(0L)
                .bookmarkCount(0L)
                .qualification(request.qualification())
                .awardScale(request.awardScale())
                .host(request.host())
                .applicationMethod(request.applicationMethod())
                .applicationEmail(request.applicationEmail())
                .hostUrl(request.hostUrl())
                .contestFields(request.contestFields())
                .writer(writer)
                .build();

        contest.updateStatus();  // 상태 설정
        return contest;
    }

    public Contest updateContest(ContestUpdateRequest request) {
        return this.toBuilder()
                .title(request.title())
                .content(request.content())
                .startDate(request.startDate())
                .endDate(request.endDate())
                .qualification(request.qualification())
                .awardScale(request.awardScale())
                .host(request.host())
                .applicationMethod(request.applicationMethod())
                .applicationEmail(request.applicationEmail())
                .hostUrl(request.hostUrl())
                .contestFields(request.contestFields())
                .build();
    }

    public void withContentImages(List<File> images) {
        this.toBuilder()
                .contentImages(images)
                .build();
    }


    // 북마크 증가/감소
    public void incrementBookmarkCount() {
        this.bookmarkCount++;
    }

    public void decrementBookmarkCount() {
        if (this.bookmarkCount > 0) {
            this.bookmarkCount--;
        }
    }

    // 조회수 증가
    public void incrementViewCount() {
        this.viewCount++;
    }

    public List<File> getContentImages() {
        return contentImages.stream()
                .filter(file -> file.getFileType() == FileType.IMAGE
                        && file.getLocation() == FileLocation.CONTEST)
                .collect(Collectors.toList());
    }

    public List<File> getAttachments() {
        return attachments.stream()
                .filter(file -> file.getFileType() == FileType.ATTACHMENT
                        && file.getLocation() == FileLocation.CONTEST)
                .collect(Collectors.toList());
    }

    public String getPosterUrl() {
        return this.getContentImages().stream()
                .filter(file -> file.getFileType() == FileType.IMAGE)
                .findFirst()
                .map(File::getUrl)
                .orElse(null);
    }

    public boolean hasPoster() {
        return this.getContentImages().stream()
                .anyMatch(file -> file.getFileType() == FileType.IMAGE);
    }
}
