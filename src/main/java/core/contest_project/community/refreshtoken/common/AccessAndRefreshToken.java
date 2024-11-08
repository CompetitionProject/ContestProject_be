package core.contest_project.community.refreshtoken.common;

public record AccessAndRefreshToken(
        String accessToken,
        String refreshToken
) {
}
