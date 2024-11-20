package core.contest_project.team.dto.response;

import core.contest_project.team.entity.Team;
import core.contest_project.user.dto.response.UserBriefProfileResponse;
import core.contest_project.user.service.data.UserDomain;
import lombok.Builder;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Builder
public record TeamResponse(
        Long teamId,
        String name,
        String description,
        UserBriefProfileResponse leader,
        List<UserBriefProfileResponse> members,
        boolean isLeader,
        boolean isMember,
        boolean hasRequestedToJoin,
        LocalDateTime createdAt,
        int currentMemberCount,
        String profileUrl
) {

    public static TeamResponse from(
            Team team,
            UserDomain leaderDomain,
            List<UserDomain> memberDomains,
            boolean isLeader,
            boolean isMember,
            boolean hasRequestedToJoin
    ) {
        return TeamResponse.builder()
                .teamId(team.getId())
                .name(team.getName())
                .description(team.getDescription())
                .leader(UserBriefProfileResponse.from(leaderDomain))
                .members(memberDomains.stream()
                        .map(UserBriefProfileResponse::from)
                        .collect(Collectors.toList()))
                .isLeader(isLeader)
                .isMember(isMember)
                .hasRequestedToJoin(hasRequestedToJoin)
                .createdAt(team.getCreatedAt())
                .currentMemberCount(team.getMembers().size())
                .profileUrl(team.getProfileImageUrl())
                .build();
    }
}
