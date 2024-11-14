package core.contest_project.file;

import core.contest_project.file.entity.File;
import core.contest_project.file.service.FileRequest;
import core.contest_project.file.service.data.FileDomain;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@Component
public class FileUtil {
    static final List<String> imageExtensions = Arrays.asList("jpeg", "jpg", "png", "gif", "bmp", "tiff", "webp", "svg", "heif");


    public static boolean isImage(String ext) {
        return imageExtensions.stream()
                .anyMatch(extension -> extension.equalsIgnoreCase(ext));
    }

    public static String createStoreFileName(String originalFilename) {
        String uuid = UUID.randomUUID().toString();
        String ext = extractExt(originalFilename);

        return uuid + "." + ext;
    }

    public static String extractExt(String urlOrFilename) {
        int pos = urlOrFilename.lastIndexOf(".");
        return urlOrFilename.substring(pos + 1);
    }



    public static List<File> toEntity(List<FileRequest> files, FileLocation location){
        List<File> fileList = new ArrayList<>();

        for (FileRequest file : files) {

            File fileEntity = File.builder()
                    .location(location)
                    .url(file.url())
                    .uploadName(file.uploadFilename())
                    .fileType(file.Type())
                    .createAt(LocalDateTime.now())
                    .build();

            fileList.add(fileEntity);

        }

        return fileList;
    }

}
