package core.contest_project.file.service.storage;

import core.contest_project.file.service.data.FileDomain;
import core.contest_project.file.service.data.FileInfo;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface FileManager {
    FileInfo upload(MultipartFile file) ;
    String getUrl(String storeFileName);
    void delete(List<String> urls);
    void setUrls(List<FileDomain> fileDomains);




}
