package core.contest_project.file.api;

import core.contest_project.file.dto.FileUrlResponse;
import core.contest_project.file.dto.FileUrlsResponse;
import core.contest_project.file.service.FileService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@RestController
@Slf4j
@RequiredArgsConstructor
public class FileController {
    private final FileService fileService;

    @PostMapping(value="/api/files/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<FileUrlResponse> uploadFile(@RequestPart(value="file") MultipartFile file){
        String url = fileService.uploadOnlyStorage(file);
        return ResponseEntity.ok(FileUrlResponse.from(url));
    }

    @PostMapping(value = "/api/files/uploads", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<FileUrlsResponse> uploadFiles(@RequestPart(value = "files") List<MultipartFile> files) {
        List<String> urls = fileService.uploadsOnlyStorage(files);
        return ResponseEntity.ok(FileUrlsResponse.from(urls));
    }
}
