package core.contest_project.file.service.db;

import core.contest_project.file.FileLocation;
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
public class FileUpdater {
    private final FileRepository fileRepository;
    private final FileCreator fileCreator;
    private final FileDeleter fileDeleter;

    public void update(Long postId, FileLocation location, List<File> filesForUpdate) {
        log.info("[FileUpdater][update]");
        List<File> oldFiles = fileRepository.findAll(postId, location);
        List<File> toAdd = new ArrayList<>();
        List<File> toDelete = new ArrayList<>();

        log.info("[filesForUpdate]");
        for (File file : filesForUpdate) {
            log.info("file.getId()= {}", file.getId());
        }

        log.info("[oldFiles]");
        for (File file : oldFiles) {
            log.info("file.getId()= {}", file.getId());
        }

        // 삭제  old-update   old 중 update 에 없으면 toDelete 이다.

        for (File file : oldFiles) {
            String url = file.getUrl();
            if (!isContainTargetUrl(filesForUpdate, url)) {
                toDelete.add(file);
            }
        }
        // 추가 update-old update 중 old 에 없으면 toAdd 이다.
        for (File file : filesForUpdate) {
            String url = file.getUrl();
            if (!isContainTargetUrl(oldFiles, url)) {
                toAdd.add(file);
            }
        }
        log.info("[toDelete]");
        for (File file : toDelete) {
            log.info("file.getId()= {}", file.getId());
        }

        log.info("[toAdd]");
        for (File file : toAdd) {
            log.info("file.getId()= {}", file.getId());
        }

        if(!toAdd.isEmpty())fileCreator.saveAll(postId, toAdd);
        if(!toDelete.isEmpty())fileDeleter.deleteAll(toDelete);


    }

    private boolean isContainTargetUrl(List<File> files,  String targetUrl){
        for (File file : files) {
            String url = file.getUrl();
            if(targetUrl.equals(url)) {
                return true;
            }
        }
        return false;
    }

}
