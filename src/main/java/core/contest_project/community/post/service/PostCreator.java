package core.contest_project.community.post.service;


import core.contest_project.community.post.service.data.PostInfo;
import core.contest_project.user.service.data.UserDomain;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class PostCreator {
    private final PostRepository postRepository;

    public Long create(PostInfo post, UserDomain user, String thumbnailUrl){
        return postRepository.save(post, user.getId(), thumbnailUrl);
    }


    public Long create(PostInfo post, UserDomain user, String thumbnailUrl, Long contestId){
        return postRepository.save(post, user.getId(), thumbnailUrl, contestId);
    }

}
