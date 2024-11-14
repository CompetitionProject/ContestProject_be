package core.contest_project.file.service.db;

import core.contest_project.file.entity.File;
import core.contest_project.file.service.FileRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
@Transactional
public class FileCreator {
    private final FileRepository fileRepository;

    public void saveAll(Long postId, List<File> files){
        fileRepository.saveAll(postId, files);
    }

}
