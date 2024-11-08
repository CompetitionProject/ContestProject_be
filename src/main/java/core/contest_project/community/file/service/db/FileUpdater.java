package core.contest_project.community.file.service.db;

import core.contest_project.community.file.FileLocation;
import core.contest_project.community.file.service.data.FileDomain;
import core.contest_project.community.file.service.storage.FileManager;
import core.contest_project.community.file.service.FileRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
@RequiredArgsConstructor
@Slf4j
public class FileUpdater {
    private final FileRepository fileRepository;
    private final FileManager fileManager;

    public void associateFilesWithPost(Long postId, Map<Long, String> orderAndUrl) {
        if (orderAndUrl == null || orderAndUrl.isEmpty()) {return;}
        Map<Long, String> orderAndStoreFileName = new ConcurrentHashMap<>();

        for (Long order : orderAndUrl.keySet()) {
            String url = orderAndUrl.get(order);
            String storeFilName = fileManager.getStoreFileName(url);
            orderAndStoreFileName.put(order, storeFilName);
        }

        fileRepository.updateAllByPost(postId, orderAndStoreFileName);
    }

    public void associateFilesWithPost(Long postId, List<FileDomain> requestFiles, FileLocation location) {

        if(requestFiles == null || requestFiles.isEmpty()) {return;}

        sortingFiles(requestFiles, location);

        fileRepository.updateAllByPost(postId, requestFiles, location);
    }

    private void sortingFiles(List<FileDomain> requestFiles,FileLocation location) {
        requestFiles.sort(Comparator.comparing(FileDomain::getOrder));

        long seq=0L;
        for (FileDomain requestFile : requestFiles) {
            String url = requestFile.getUrl();
            String storeFilName = fileManager.getStoreFileName(url);
            requestFile.setStoreFileName(storeFilName);
            requestFile.setOrder(seq++);
            requestFile.setFileLocation(location);
        }
    }


}
