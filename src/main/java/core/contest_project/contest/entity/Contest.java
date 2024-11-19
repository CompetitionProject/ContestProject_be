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

    /*@OneToMany(mappedBy = "contest")
    private List<IndividualAwaiter> individualAwaiters = new ArrayList<>();

    // 실시간 대기자 수가 필요한 경우를 위한 메서드
    public long getActiveIndividualAwaiterCount() {
        return individualAwaiters.stream()
                .filter(awaiter -> awaiter.isWaiting())
                .count();
    }*/

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

        contest.updateContestStatus();  // 상태 설정
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
