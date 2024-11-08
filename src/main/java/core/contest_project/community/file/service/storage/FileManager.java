package core.contest_project.community.file.service.storage;

import core.contest_project.community.file.service.data.FileDomain;
import core.contest_project.community.file.service.data.FileInfo;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public interface FileManager {
    FileInfo upload(MultipartFile file) ;

    String getUrl(String storeFileName);

    void delete(List<String> urls);

    void setUrls(List<FileDomain> fileDomains);


    default String createStoreFileName(String originalFilename) {
        String uuid = UUID.randomUUID().toString();
        String ext = extractExt(originalFilename);

        return uuid + "." + ext;
    }

    default String extractExt(String originalFilename) {
        int pos = originalFilename.lastIndexOf(".");
        return originalFilename.substring(pos + 1);
    }

    default String getStoreFileName(String url) {
        if (url == null || url.isEmpty()) {
            throw new IllegalArgumentException("URL cannot be null or empty");
        }
        return url.substring(url.lastIndexOf('/') + 1);
    }

    default List<String> getUrls(List<FileDomain> files){
        List<String> urls = new ArrayList<>();
        for (FileDomain file : files) {
            urls.add(file.getUrl());
        }
        return urls;
    }

}
