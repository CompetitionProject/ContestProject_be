package core.contest_project.community.post.service.data;

import core.contest_project.file.service.data.FileDomain;
import core.contest_project.user.service.data.UserDomain;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
@AllArgsConstructor
public class PostUpdateDomain {
    private Long id;

    private UserDomain writer;

    private PostInfo info;

    private List<FileDomain> files;



}
