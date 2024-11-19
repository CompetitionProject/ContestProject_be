package core.contest_project.refreshtoken.service;

import core.contest_project.refreshtoken.entity.RefreshToken;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class RefreshTokenService {
    private final RefreshTokenRepository refreshTokenRepository;

    public Long save(RefreshToken refreshToken) {
        return refreshTokenRepository.save(refreshToken);
    }

    public RefreshToken findByUserIdAndBlacklistIsFalse(Long userId){
        return refreshTokenRepository.findByUserIdAndBlacklistIsFalse(userId);
    }

    public RefreshToken findByUserIdAndRefreshToken(Long userId, String refreshToken){
        return refreshTokenRepository.findByUserIdAndRefreshToken(userId, refreshToken);
    }



}
