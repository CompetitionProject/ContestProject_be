package core.contest_project.community.file.service;

import core.contest_project.community.file.FileLocation;
import core.contest_project.community.file.FileType;
import core.contest_project.community.file.service.data.FileDomain;
import core.contest_project.community.file.service.data.FileInfo;
import core.contest_project.community.file.service.db.FileCreator;
import core.contest_project.community.file.service.db.FileDeleter;
import core.contest_project.community.file.service.db.FileReader;
import core.contest_project.community.file.service.db.FileUpdater;
import core.contest_project.community.file.service.storage.FileManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class FileService {
    private final FileCreator fileCreator;
    private final FileReader fileReader;
    private final FileUpdater fileUpdater;
    private final FileDeleter fileDeleter;
    private final FileManager fileManager;

    /**
     *
     * @param file
     * @param type
     * 이미지인지 첨부파일인지 구분합니다.
     *
     * @param location
     * 게시글 파일인지 공모전 파일인지 구분합니다.
     *
     * @return
     * url 을 리턴합니다.
     *
     * @apiNote
     * 파일 하나를 storage(S3 또는 로컬)에 저장하고 DB에 저장합니다.
     * DB 에서 아직 파일과 게시글(또는 공모전)의 연관관계를 맺지 않습니다.
     * 실제 게시글 (또는 공모전)을 작성하기 누를 때 연관관계가 생깁니다.
     */

    public String upload(MultipartFile file, FileType type, FileLocation location) {
        // 저장소에 파일 저장.
        FileInfo fileInfo = fileManager.upload(file);
        fileInfo.setFileType(type);
        fileInfo.setLocation(location);

        // DB에 파일 저장.
        fileCreator.save(fileInfo);

        return fileManager.getUrl(fileInfo.getStoreFileName());
    }


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

    public List<FileDomain> getFiles(Long PostId){
        return fileReader.getFiles(PostId);
    }

    public FileDomain getThumbnail(Long PostId){
        return fileReader.getThumbnail(PostId);
    }


    /**
     *
     * @param postId
     * 게시글 또는 공모전의 ID를 의미합니다.
     *
     * @param requestFiles
     * @param oldFiles
     * @param location
     *
     * @apiNote
     * 게시글(또는 공모전)과 관련된 파일을 oldFiles -> requestFiles 로 업데이트합니다.
     * 업데이트란 (1)파일 삭제와 (2)DB에 있는 파일과 게시글(또는 공모전)간 연관관계를 맺습니다.
     * 파일 추가는 위 upload 메소드에서 했기 떄문에 여기서는 하지 않습니다.
     * 이미 추가한 파일과 게시글(또는 공모전)과의 관계만 설정해주면 됩니다.
     * 삭제 시 storage 와 DB 에서 모두 삭제합니다.
     */
    public void update(Long postId, List<FileDomain> requestFiles, List<FileDomain> oldFiles, FileLocation location) {
        setUrls(oldFiles);
        List<String> oldUrls = oldFiles.stream().map(FileDomain::getUrl).toList();
        List<String> requestUrls = requestFiles.stream().map(FileDomain::getUrl).toList();

        List<String> toDelete = new ArrayList<>(oldUrls);
        toDelete.removeAll(requestUrls);

        // 삭제
        if (!toDelete.isEmpty()) {
            fileManager.delete(toDelete);
            //fileDeleter.delete(postId, toDelete);
            fileDeleter.deleteFilesByUrl(toDelete);
        }


        // 업로드할 때 이미 DB 에 들어감.
        fileUpdater.associateFilesWithPost(postId, requestFiles, location);
    }

    private void setUrls(List<FileDomain> files){
        for (FileDomain file : files) {
            String url = fileManager.getUrl(file.getStoreFileName());
            file.setUrl(url);
        }
    }
}
