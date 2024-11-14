package core.contest_project.file.service;

import core.contest_project.common.error.file.FileErrorResult;
import core.contest_project.common.error.file.FileException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class FileService {
    private final S3Service s3Service;
    /**
     *
     * @param file
     * @return
     *
     * DB에 저장하지 않고 오직 storage (S3 또는 로컬)에 저장합니다.
     * 프로필 이미지 등록과 채팅 시 이미지 보내기에 사용할 예정입니다.
     */
    public String uploadOnlyStorage(MultipartFile file) {
        try {
            return s3Service.uploadFile(file);
        } catch (IOException e) {
            log.error("File upload failed", e);
            throw new FileException(FileErrorResult.FILE_UPLOAD_ERROR);
        }
    }

    public List<String> uploadsOnlyStorage(List<MultipartFile> files) {
        return s3Service.uploadFiles(files);
    }
}
