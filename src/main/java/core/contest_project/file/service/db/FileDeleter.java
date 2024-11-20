package core.contest_project.file.service.db;

import core.contest_project.file.entity.File;
import core.contest_project.file.service.FileRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class FileDeleter {
    private final FileRepository fileRepository;

    public void deleteByPostId(Long postId){
        fileRepository.deleteByPostId(postId);
    }

    public void deleteByContestId(Long contestId) {
        fileRepository.deleteByContestId(contestId);
    }


    public void deleteAll(List<File> files){
        List<Long> ids = new ArrayList<>();
        for (File file : files) {
            ids.add(file.getId());
        }
        fileRepository.deleteAll(ids);
    }
}
