package core.contest_project.refreshtoken.service;

import core.contest_project.refreshtoken.entity.RefreshToken;

public interface RefreshTokenRepository {
    Long save(String refreshToken, Long userId);
    Long save(RefreshToken refreshToken);
    RefreshToken findByUserIdAndBlacklistIsFalse(Long userId);
    RefreshToken findByUserIdAndRefreshToken(Long userId, String refreshToken);
}
