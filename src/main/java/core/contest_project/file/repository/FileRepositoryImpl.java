package core.contest_project.file.repository;

import core.contest_project.file.FileLocation;
import core.contest_project.file.service.data.FileInfo;
import core.contest_project.file.entity.File;
import core.contest_project.file.service.FileRepository;
import core.contest_project.community.post.entity.Post;
import core.contest_project.community.post.repository.PostJpaRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Repository
@RequiredArgsConstructor
@Slf4j
@Transactional
public class FileRepositoryImpl implements FileRepository {
    private final FileJpaRepository fileJpaRepository;
    private final PostJpaRepository postJpaRepository;


    @Override
    public Long save(FileInfo fileInfo) {
        File file = File.builder()
                .post(null)
                .url(fileInfo.getStoreFileName())
                .uploadName(fileInfo.getUploadFileName())
                .createAt(LocalDateTime.now())
                .fileType(fileInfo.getFileType())
                .location(fileInfo.getLocation())
                .build();

        return fileJpaRepository.save(file).getId();
    }

    @Override
    public void saveAll(Long postId, List<File> files) {
        FileLocation location = files.get(0).getLocation();

        if(location==FileLocation.POST){
            Post post = postJpaRepository.getReferenceById(postId);
            for (File file : files) {
                file.setPost(post);
            }

        }
        else if(location==FileLocation.CONTEST){
            // 위와 동일.
        }

        fileJpaRepository.saveAll(files);
    }


    @Override
    public List<File> findAll(Long postId, FileLocation location) {
        return fileJpaRepository.findAllByPostIdAndLocation(postId, location);

    }

    @Override
    public void deleteAllByPostId(Long postId, List<String> storeFileNames) {
        fileJpaRepository.deleteAllByPostId(postId, storeFileNames);
    }


    @Override
    public void delete(Long postId) {
        fileJpaRepository.delete(postId);

    }

    @Override
    public void deleteAll(List<Long> ids) {

        fileJpaRepository.deleteAll(ids);
    }





}
