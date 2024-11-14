package core.contest_project.file.service.db;

import core.contest_project.file.FileLocation;
import core.contest_project.file.entity.File;
import core.contest_project.file.service.FileRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class FileReader {
    private final FileRepository fileRepository;

    public List<File> getFiles(Long postId, FileLocation location){
        return fileRepository.findAll(postId, location);
    }
}
