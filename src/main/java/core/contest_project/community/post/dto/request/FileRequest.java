package core.contest_project.community.post.dto.request;

import core.contest_project.community.file.FileLocation;
import core.contest_project.community.file.service.data.FileDomain;
import core.contest_project.community.file.service.data.FileInfo;

public record FileRequest(
        Long order,
        String url
) {

    public FileDomain toFileDomain(){
        FileInfo info = FileInfo.builder()
                .location(FileLocation.POST)
                .build();
        return FileDomain.builder()
                .order(order)
                .info(info)
                .url(url).build();
    }
}
