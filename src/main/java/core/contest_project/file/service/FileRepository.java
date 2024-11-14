package core.contest_project.file.service;

import core.contest_project.file.FileLocation;
import core.contest_project.file.entity.File;
import core.contest_project.file.service.data.FileInfo;

import java.util.List;

public interface FileRepository {
    Long save(FileInfo fileInfo);
    void saveAll(Long postId, List<File> files);

    List<File> findAll(Long postId, FileLocation location);

    void deleteAllByPostId(Long postId, List<String> storeFileNames);
    void delete(Long postId);
    void deleteAll(List<Long> ids);
}
