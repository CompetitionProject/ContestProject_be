package core.contest_project.refreshtoken.repository;

import core.contest_project.refreshtoken.service.RefreshTokenRepository;
import core.contest_project.refreshtoken.entity.RefreshToken;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
@Slf4j
public class RefreshTokenImpl implements RefreshTokenRepository {
    private final RefreshTokenJpaRepository refreshTokenJpaRepository;

    @Override
    public Long save(String refreshToken, Long userId) {
        RefreshToken token = RefreshToken.builder()
                .refreshToken(refreshToken)
                .userId(userId)
                .isBlacklist(false)
                .build();

        return refreshTokenJpaRepository.save(token).getId();
    }

    @Override
    public Long save(RefreshToken refreshToken) {
        return refreshTokenJpaRepository.save(refreshToken).getId();
    }



    @Override
    public RefreshToken findByUserIdAndBlacklistIsFalse(Long userId) {
        return refreshTokenJpaRepository.findByUserIdAndBlacklistIsFalse(userId).orElseThrow(() -> new IllegalArgumentException("RefreshToken not found"));
    }

    @Override
    public RefreshToken findByUserIdAndRefreshToken(Long userId, String refreshToken) {
        return refreshTokenJpaRepository.findByUserIdAndRefreshToken(userId, refreshToken).orElseThrow(() -> new IllegalArgumentException("RefreshToken not found"));
    }
}
