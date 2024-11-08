package core.contest_project.community.refreshtoken.service;

public interface RefreshTokenRepository {
    Long save(String refreshToken, Long userId);
}
