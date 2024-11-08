package core.contest_project.community.file.api;

import core.contest_project.community.file.FileLocation;
import core.contest_project.community.file.FileType;
import core.contest_project.community.file.dto.FileResponse;
import core.contest_project.community.file.service.FileService;
import core.contest_project.community.file.service.data.FileDomain;
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

    @PostMapping(value="/api/post-images", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Map> postImages(@RequestPart(value="file") MultipartFile file){

        String url = fileService.upload(file, FileType.IMAGE, FileLocation.POST);
        Map<String, String> map = new ConcurrentHashMap<>();
        map.put("url", url);

        return ResponseEntity.ok(map);
    }

    @PostMapping(value="/api/post-attachments", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Map> postAttachments(@RequestPart(value="file") MultipartFile file){

        String url = fileService.upload(file, FileType.ATTACHMENT, FileLocation.POST);
        Map<String, String> map = new ConcurrentHashMap<>();
        map.put("url", url);

        return ResponseEntity.ok(map);
    }

    @PostMapping(value="/api/contest-images", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Map> contestImages(@RequestPart(value="file") MultipartFile file){

        String url = fileService.upload(file, FileType.IMAGE, FileLocation.CONTEST);
        Map<String, String> map = new ConcurrentHashMap<>();
        map.put("url", url);

        return ResponseEntity.ok(map);
    }

    @PostMapping(value="/api/contest-attachments", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Map> contestAttachments(@RequestPart(value="file") MultipartFile file){

        String url = fileService.upload(file, FileType.ATTACHMENT, FileLocation.CONTEST);
        Map<String, String> map = new ConcurrentHashMap<>();
        map.put("url", url);

        return ResponseEntity.ok(map);
    }

    @PostMapping(value="/api/profile-image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Map> storeProfileImage(@RequestPart(value="file") MultipartFile file){
        String url = fileService.uploadOnlyStorage(file);
        Map<String, String> map = new ConcurrentHashMap<>();
        map.put("profileImage", url);
        return ResponseEntity.ok(map);
    }


    // == test == //

    @GetMapping("test/files/{post-id}")
    public ResponseEntity<FileResponse> getFiles(@PathVariable("post-id") Long postId) {
        List<FileDomain> files = fileService.getFiles(postId);
        FileResponse fileResponse = FileResponse.fromDomain(files);

        return ResponseEntity.ok(fileResponse);
    }

    @GetMapping("/test/files/thumbnail/{post-id}")
    public ResponseEntity<?> getThumbnail(@PathVariable("post-id") Long postId) {
        FileDomain thumbnail = fileService.getThumbnail(postId);
        return ResponseEntity.ok(thumbnail);
    }


}
