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
}
