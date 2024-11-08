package core.contest_project.community.file.service.db;

import core.contest_project.community.file.service.FileRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class FileDeleter {
    private final FileRepository fileRepository;

    public void delete(Long postId){
        fileRepository.delete(postId);
    }

    public void delete(Long postId, List<String> urls){
        if(urls == null || urls.isEmpty()){return;}
        // DB 삭제
        List<String> storeNames = getStoreNames(urls);

        fileRepository.deleteAllByPostId(postId,storeNames);
    }

    public void deleteFilesByUrl(List<String> urls){
        if(urls == null || urls.isEmpty()){return;}
        List<String> storeNames = getStoreNames(urls);
        fileRepository.deleteAllByStoreFileName(storeNames);
    }

    private List<String> getStoreNames(List<String> fullPaths){
        List<String> storeNames = new ArrayList<>();
        for (String fullPath : fullPaths){
            File file = new File(fullPath);
            storeNames.add(file.getName());
        }

        return storeNames;
    }
}
