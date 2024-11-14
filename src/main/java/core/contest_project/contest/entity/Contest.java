package core.contest_project.contest.entity;

import core.contest_project.common.error.file.FileErrorResult;
import core.contest_project.common.error.file.FileException;
import core.contest_project.file.FileLocation;
import core.contest_project.file.FileType;
import core.contest_project.file.entity.File;
import core.contest_project.user.entity.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
@Entity
public class Contest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "contest_id")
    private Long id;

    private String title;

    private String content;

    // 본문 이미지
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "contest")
    private List<File> contentImages = new ArrayList<>();

    // 포스터 이미지
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "contest")
    private List<File> posterImage = new ArrayList<>();

    // 첨부 파일(지원서)
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "contest")
    private List<File> attachments = new ArrayList<>();

    //접수일
    @Column(nullable = false)
    private LocalDateTime startDate;

    //마감일
    @Column(nullable = false)
    private LocalDateTime endDate;

    // 조회수
    @Column(name = "view_count")
    private Long viewCount = 0L;

    // 북마크수
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

//    @OneToMany(mappedBy = "contest", cascade = CascadeType.ALL, orphanRemoval = true)
//    private List<Awaiter> awaiters = new ArrayList<>(); // 대기자 목록

    // 모집 상태 결정
    public void updateContestStatus() {
        LocalDateTime now = LocalDateTime.now();

        if (now.isBefore(startDate)) {
            this.contestStatus = ContestStatus.NOT_STARTED;
        } else if (now.isAfter(endDate)) {
            this.contestStatus = ContestStatus.CLOSED;
        } else {
            this.contestStatus = ContestStatus.IN_PROGRESS;
        }
    }

    public static Contest createContest(String title,
                                        String content,
                                        LocalDateTime startDate,
                                        LocalDateTime endDate,
                                        String qualification,
                                        String awardScale,
                                        String host,
                                        ContestApplicationMethod applicationMethod,
                                        String applicationEmail,
                                        String hostUrl,
                                        List<ContestField> contestFields,
                                        User writer) {
        Contest contest = Contest.builder()
                .title(title)
                .content(content)
                .startDate(startDate)
                .endDate(endDate)
                .viewCount(0L)
                .bookmarkCount(0L)
                .qualification(qualification)
                .awardScale(awardScale)
                .host(host)
                .applicationMethod(applicationMethod)
                .applicationEmail(applicationEmail)
                .hostUrl(hostUrl)
                .contestFields(contestFields)
                .writer(writer)
                .build();

        contest.updateContestStatus();  // 상태 설정
        return contest;
    }

    public void updateContest(String title,
                              String content,
                              LocalDateTime startDate,
                              LocalDateTime endDate,
                              String qualification,
                              String awardScale,
                              String host,
                              ContestApplicationMethod applicationMethod,
                              String applicationEmail,
                              String hostUrl,
                              List<ContestField> contestFields) {
        // 기본 정보 업데이트
        this.title = title;
        this.content = content;
        this.startDate = startDate;
        this.endDate = endDate;
        this.qualification = qualification;
        this.awardScale = awardScale;
        this.host = host;
        this.applicationMethod = applicationMethod;
        this.applicationEmail = applicationEmail;
        this.hostUrl = hostUrl;

        // contestFields 업데이트
        this.contestFields.clear();
        this.contestFields.addAll(contestFields);
    }

    public void withContentImages(List<File> images) {
        this.toBuilder()
                .contentImages(images)
                .build();
    }

    public void withPoster(List<File> posterImage) {
        if (posterImage.size() > 1) {
            throw new FileException(FileErrorResult.POSTER_ALREADY_EXISTS);
        }
        this.posterImage = posterImage;
    }

    public void withAttachments(List<File> attachments) {
        this.toBuilder()
                .attachments(attachments)
                .build();
    }

    // 마감 여부 확인
    public boolean isExpired() {
        return LocalDateTime.now().isAfter(endDate);
    }

    // 시작 여부 확인
    public boolean isStarted() {
        return LocalDateTime.now().isAfter(startDate);
    }

    // 진행 상태 업데이트
    public void updateStatus() {
        if (!isStarted()) {
            this.contestStatus = ContestStatus.NOT_STARTED;
        } else if (isExpired()) {
            this.contestStatus = ContestStatus.CLOSED;
        } else {
            this.contestStatus = ContestStatus.IN_PROGRESS;
        }
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

    public List<File> getPosterImage() {
        return posterImage.stream()
                .filter(file -> file.getFileType() == FileType.POSTER
                        && file.getLocation() == FileLocation.CONTEST)
                .collect(Collectors.toList());
    }

    public boolean hasPoster() {
        return this.posterImage != null && !this.posterImage.isEmpty();
    }
}
