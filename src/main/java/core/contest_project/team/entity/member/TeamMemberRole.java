package core.contest_project.team.entity.member;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum TeamMemberRole {

    LEADER("팀장"),
    MEMBER("팀원");

    private final String description;
}
