package core.contest_project.file.service.storage;

import core.contest_project.file.service.data.FileDomain;
import core.contest_project.file.service.data.FileInfo;
import core.contest_project.file.FileUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class LocalFileManager implements FileManager {

    @Value("${base.dir}")
    private String baseDir;

    @Override
    public FileInfo upload(MultipartFile file)  {
        if(file.isEmpty()){return null;}

        // storeFileName -> uploadFileName
        String originalFilename = file.getOriginalFilename();
        String storeFileName = FileUtil.createStoreFileName(originalFilename);
        String url = getUrl(storeFileName);

        try {
            file.transferTo(new File(url));
        } catch (IOException e) {
            e.printStackTrace();
            log.error(e.getMessage());
            throw new RuntimeException(e);
        }

        return FileInfo.builder()
                .storeFileName(storeFileName)
                .uploadFileName(originalFilename)
                .build();
    }



    @Override
    public String getUrl(String storeFileName) {
        if(storeFileName==null||storeFileName.isEmpty()){return null;}
        return baseDir+"file/"+ storeFileName;
    }

    @Override
    public void delete(List<String> urls) {
        for (String url : urls) {
            File file = new File(url);
            file.delete();
        }
    }

    @Override
    public void setUrls(List<FileDomain> fileDomains) {
        for (FileDomain fileDomain : fileDomains) {
            String url = getUrl(fileDomain.getInfo().getStoreFileName());
            fileDomain.setUrl(url);
        }
    }


}
