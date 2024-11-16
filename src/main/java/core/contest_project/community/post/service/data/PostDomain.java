package core.contest_project.community.post.service.data;

import core.contest_project.file.service.FileResponse;
import core.contest_project.user.service.data.UserDomain;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Builder
@AllArgsConstructor
public class PostDomain {
    private Long id;

    private UserDomain writer;
    private boolean isWriter;

    private PostInfo info;
    private Long viewCount;
    private LocalDateTime createAt;

    private Long scrapCount;
    private boolean isScraped;

    private Long likeCount;
    private boolean isLiked;

    private boolean isNotified;  // 이거는 알람 구현할 때 같이하자.

    private Long nextAnonymousSeq;

    private List<FileResponse> files;

    public void increaseViewCount() {
        viewCount++;
    }

    public void setIsWriter(boolean writer) {
        isWriter = writer;
    }


    public void setScrapCount(Long scrapCount) {
        this.scrapCount = scrapCount;
    }

    public void setIsScraped(boolean scraped) {
        isScraped = scraped;
    }

    public void setLikeCount(Long likeCount) {
        this.likeCount = likeCount;
    }

    public void setIsLiked(boolean liked) {
        isLiked = liked;
    }

    public boolean isWriter() {
        return isWriter;
    }

    public void setFiles(List<FileResponse> files) {
        this.files = files;
    }
}
