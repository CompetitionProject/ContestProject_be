package core.contest_project.refreshtoken.service;

public interface RefreshTokenRepository {
    Long save(String refreshToken, Long userId);
}
