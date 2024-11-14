package core.contest_project.file.api;

import core.contest_project.file.service.FileService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@RestController
@Slf4j
@RequiredArgsConstructor
public class FileController {
    private final FileService fileService;


    @PostMapping(value="/api/files", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Map> uploadFile(@RequestPart(value="file") MultipartFile file){
        String url = fileService.uploadOnlyStorage(file);
        Map<String, String> map = new ConcurrentHashMap<>();
        map.put("url", url);
        return ResponseEntity.ok(map);
    }


}
