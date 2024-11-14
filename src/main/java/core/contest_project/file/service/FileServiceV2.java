package core.contest_project.file.service;

import core.contest_project.common.error.file.FileErrorResult;
import core.contest_project.common.error.file.FileException;
import core.contest_project.contest.entity.Contest;
import core.contest_project.file.FileLocation;
import core.contest_project.file.FileType;
import core.contest_project.file.entity.File;
import core.contest_project.file.repository.FileJpaRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class FileServiceV2 {

    private final S3Service s3Service;
    private final FileJpaRepository fileRepository;

    public List<File> uploadFile(Contest contest, MultipartFile file, FileType fileType) {
        return uploadFiles(contest, List.of(file), fileType);
    }

    public List<File> uploadFiles(Contest contest, List<MultipartFile> multipartFiles, FileType fileType) {
        return multipartFiles.stream()
                .map(file -> uploadSingleFile(contest, file, fileType))
                .map(fileRepository::save)
                .collect(Collectors.toList());
    }

    private File uploadSingleFile(Contest contest, MultipartFile file, FileType fileType) {
        try {
            String fileUrl = s3Service.uploadFile(file);

            return File.builder()
                    .contest(contest)
                    .url(fileUrl)
                    .uploadName(file.getOriginalFilename())
                    .fileType(fileType)
                    .location(FileLocation.CONTEST)
                    .createAt(LocalDateTime.now())
                    .build();
        } catch (IOException e) {
            throw new FileException(FileErrorResult.FILE_UPLOAD_ERROR);
        }
    }

    // 파일 ID로 삭제
    public void deleteFilesByFileId(Long fileId) {
        File file = fileRepository.findById(fileId)
                .orElseThrow(() -> new FileException(FileErrorResult.FILE_NOT_FOUND));

        try {
            s3Service.deleteFile(file.getUrl());
            fileRepository.delete(file);
        } catch (Exception e) {
            throw new FileException(FileErrorResult.FILE_DELETE_ERROR);
        }
    }

    // 여러 파일 ID로 삭제
    public void deleteFilesByFileIds(List<Long> fileIds) {
        List<File> files = fileRepository.findAllById(fileIds);

        if (files.isEmpty()) {
            throw new FileException(FileErrorResult.FILE_NOT_FOUND);
        }

        try {
            List<String> fileUrls = files.stream()
                    .map(File::getUrl)
                    .collect(Collectors.toList());

            s3Service.deleteFiles(fileUrls);
            fileRepository.deleteAll(files);
        } catch (Exception e) {
            throw new FileException(FileErrorResult.FILE_DELETE_ERROR);
        }
    }
}
