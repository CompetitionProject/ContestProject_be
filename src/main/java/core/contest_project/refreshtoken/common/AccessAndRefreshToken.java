package core.contest_project.refreshtoken.common;

public record AccessAndRefreshToken(
        String accessToken,
        String refreshToken
) {
}
