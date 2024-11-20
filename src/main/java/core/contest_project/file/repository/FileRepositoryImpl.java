package core.contest_project.file.repository;

import core.contest_project.contest.entity.Contest;
import core.contest_project.contest.repository.ContestRepository;
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
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
@Slf4j
@Transactional
public class FileRepositoryImpl implements FileRepository {
    private final FileJpaRepository fileJpaRepository;
    private final PostJpaRepository postJpaRepository;
    private final ContestRepository contestRepository;

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
        List<File> updatedFiles;

        if(location==FileLocation.POST){
            Post post = postJpaRepository.getReferenceById(postId);
            for (File file : files) {
                file.setPost(post);
            }

        }
        else if(location==FileLocation.CONTEST){
            Contest contest = contestRepository.getReferenceById(postId);
            updatedFiles = files.stream()
                    .map(file -> file.toBuilder()
                            .contest(contest)
                            .build())
                    .collect(Collectors.toList());
            fileJpaRepository.saveAll(updatedFiles);
            return;
        }

        fileJpaRepository.saveAll(files);
    }


    @Override
    public List<File> findAll(Long postId, FileLocation location) {
        if (location == FileLocation.POST) {
            return fileJpaRepository.findAllByPostIdAndLocation(postId, location);
        } else {
            return fileJpaRepository.findAllByContestIdAndLocation(postId, location);
        }

    }

    @Override
    public void deleteAllByPostId(Long postId, List<String> storeFileNames) {
        fileJpaRepository.deleteAllByPostId(postId, storeFileNames);
    }


    @Override
    public void deleteByPostId(Long postId) {
        fileJpaRepository.deleteByPostId(postId);
    }

    @Override
    public void deleteByContestId(Long contestId) {
        fileJpaRepository.deleteByContestId(contestId);
    }

    @Override
    public void deleteAll(List<Long> ids) {
        fileJpaRepository.deleteAll(ids);
    }
}
