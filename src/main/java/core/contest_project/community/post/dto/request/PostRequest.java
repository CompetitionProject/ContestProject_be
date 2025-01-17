package core.contest_project.community.post.dto.request;

import core.contest_project.file.service.FileRequest;
import core.contest_project.community.post.service.data.PostInfo;
import jakarta.validation.constraints.NotBlank;

import java.util.List;

public record PostRequest(
        @NotBlank(message= "title is not empty.")
        String title,
        String contestTitle,
        String content,
        List<FileRequest> files
) {

    public PostInfo toPostInfo(){
        return new PostInfo(title, contestTitle, content);
    }

}
