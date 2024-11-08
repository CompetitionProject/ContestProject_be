package core.contest_project.community.file.service.db;

import core.contest_project.community.file.service.data.FileDomain;
import core.contest_project.community.file.service.storage.FileManager;
import core.contest_project.community.file.service.FileRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class FileReader {
    private final FileRepository fileRepository;
    private final FileManager fileManager;

    public List<FileDomain> getFiles(Long postId){
        List<FileDomain> files = fileRepository.findAllByPostId(postId);
        for (FileDomain file : files) {
            String url = fileManager.getUrl(file.getInfo().getStoreFileName());
            file.setUrl(url);
        }

        return files;
    }

    public FileDomain getThumbnail(Long postId){
        return fileRepository.findThumbnailByPostId(postId);
    }
}
