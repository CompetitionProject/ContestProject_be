package core.contest_project.file.service;

import core.contest_project.file.service.data.FileInfo;
import core.contest_project.file.service.storage.FileManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
@Slf4j
public class FileService {
    private final FileManager fileManager;

    /**
     *
     * @param file
     * @return
     *
     * DB에 저장하지 않고 오직 storage (S3 또는 로컬)에 저장합니다.
     * 프로필 이미지 등록과 채팅 시 이미지 보내기에 사용할 예정입니다.
     */
    public String uploadOnlyStorage(MultipartFile file) {
        FileInfo fileInfo = fileManager.upload(file);
        return fileManager.getUrl(fileInfo.getStoreFileName());
    }



}
