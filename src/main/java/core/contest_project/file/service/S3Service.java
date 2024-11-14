package core.contest_project.file.service;

import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.*;
import core.contest_project.common.error.file.FileErrorResult;
import core.contest_project.common.error.file.FileException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class S3Service {

    private final AmazonS3Client amazonS3Client;

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    public String uploadFile(MultipartFile file) throws IOException {
        validateFile(file);  // 파일 유효성 검증

        try {
            String fileName = createFileName(file.getOriginalFilename());
            ObjectMetadata metadata = createMetadata(file);

            try (InputStream inputStream = file.getInputStream()) {
                amazonS3Client.putObject(new PutObjectRequest(bucket, fileName, inputStream, metadata)
                        .withCannedAcl(CannedAccessControlList.PublicRead));
            }

            return amazonS3Client.getUrl(bucket, fileName).toString();
        } catch (AmazonS3Exception e) {
            log.error("AWS S3 upload error: {}", e.getMessage(), e);
            throw new FileException(FileErrorResult.FILE_UPLOAD_ERROR);
        } catch (IOException e) {
            log.error("File read error: {}", e.getMessage(), e);
            throw new FileException(FileErrorResult.FILE_UPLOAD_ERROR);
        }
    }
    public List<String> uploadFiles(List<MultipartFile> files) {
        if (files == null || files.isEmpty()) {
            throw new FileException(FileErrorResult.EMPTY_FILE);
        }

        return files.stream()
                .filter(file -> !file.isEmpty())
                .map(file -> {
                    try {
                        return uploadFile(file);
                    } catch (IOException e) {
                        throw new FileException(FileErrorResult.FILE_UPLOAD_ERROR);
                    }
                })
                .collect(Collectors.toList());
    }

    public void deleteFile(String fileUrl) {
        try {
            String fileName = extractFileNameFromUrl(fileUrl);
            amazonS3Client.deleteObject(new DeleteObjectRequest(bucket, fileName));
        } catch (AmazonS3Exception e) {
            throw new FileException(FileErrorResult.FILE_DELETE_ERROR);
        }
    }

    public void deleteFiles(List<String> fileUrls) {
        try {
            DeleteObjectsRequest deleteObjectsRequest = new DeleteObjectsRequest(bucket)
                    .withKeys(fileUrls.stream()
                            .map(this::extractFileNameFromUrl)
                            .map(fileName -> new DeleteObjectsRequest.KeyVersion(fileName))
                            .collect(Collectors.toList()));

            amazonS3Client.deleteObjects(deleteObjectsRequest);
        } catch (AmazonS3Exception e) {
            throw new FileException(FileErrorResult.FILE_DELETE_ERROR);
        }
    }

    private ObjectMetadata createMetadata(MultipartFile file) {
        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentType(file.getContentType());
        metadata.setContentLength(file.getSize());
        return metadata;
    }

    private String createFileName(String originalFilename) {
        return UUID.randomUUID().toString() + "_" + originalFilename;
    }

    private void validateFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new FileException(FileErrorResult.EMPTY_FILE);
        }

        // 파일 크기 검증 (10MB)
        if (file.getSize() > 10 * 1024 * 1024) {
            throw new FileException(FileErrorResult.FILE_SIZE_EXCEED);
        }

        String contentType = file.getContentType();
        if (contentType == null || !isValidContentType(contentType)) {
            throw new FileException(FileErrorResult.INVALID_FILE_TYPE);
        }
    }

    private boolean isValidContentType(String contentType) {
        // 이미지
        if (contentType.startsWith("image/")) {
            return true;
        }

        // 문서 파일
        if (contentType.equals("application/pdf") ||
                contentType.equals("application/msword") ||  // .doc
                contentType.equals("application/vnd.openxmlformats-officedocument.wordprocessingml.document") ||  // .docx
                contentType.equals("application/vnd.ms-excel") ||  // .xls
                contentType.equals("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet") ||  // .xlsx
                contentType.equals("application/vnd.ms-powerpoint") ||  // .ppt
                contentType.equals("application/vnd.openxmlformats-officedocument.presentationml.presentation")) {  // .pptx
            return true;
        }

        return false;
    }

    private String extractFileNameFromUrl(String fileUrl) {
        return fileUrl.substring(fileUrl.lastIndexOf("/") + 1);
    }

}
