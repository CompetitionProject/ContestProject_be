package core.contest_project.common.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import core.contest_project.refreshtoken.common.JwtTokenUtil;
import core.contest_project.refreshtoken.entity.RefreshToken;
import core.contest_project.refreshtoken.repository.RefreshTokenJpaRepository;
import core.contest_project.user.entity.User;
import core.contest_project.user.repository.UserJpaRepository;
import core.contest_project.user.service.UserReader;
import core.contest_project.user.service.data.UserDomain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Component
@RequiredArgsConstructor
@Slf4j
@Transactional
public class CustomAuthenticationSuccessHandler implements AuthenticationSuccessHandler {
    private final UserJpaRepository userJpaRepository; // jpaRepository 말고 다른 걸로 수정?
    private final RefreshTokenJpaRepository refreshTokenJpaRepository;
    private final ObjectMapper objectMapper;


    /**
     *
     * @param request
     * @param response
     * @param authentication
     * @throws IOException
     *
     * @apiNote
     *
     */

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException {
        log.info("[CustomAuthenticationSuccessHandler][onAuthenticationSuccess]");

        OAuth2User oAuth2User = (OAuth2User)authentication.getPrincipal();
        KaKaoInfo kaKaoInfo = getKaKaoInfoFrom(oAuth2User);
        Optional<User> user= userJpaRepository.findByEmail(kaKaoInfo.getEmail());
        Map<String, String> jsonResponse = new HashMap<>();

        log.info("KaKaoInfo= {}", kaKaoInfo);

        if(user.isPresent()){
            Long userId = user.get().getId();
            log.info("기존 유저");
            // ===
            String accessToken = JwtTokenUtil.generateAccessToken(userId);
            String refreshToken = JwtTokenUtil.generateRefreshToken(userId);
            // ===
            log.info("accessToken: " + accessToken);
            log.info("refreshToken: " + refreshToken);


            Optional<RefreshToken> findToken = refreshTokenJpaRepository.findByUserIdAndBlacklistIsFalse(userId);
            RefreshToken token;
            if(findToken.isEmpty()){
                log.info("refreshToken 없음");
                token=RefreshToken.builder()
                        .refreshToken(refreshToken)
                        .userId(userId)
                        .isBlacklist(false)
                        .build();

                refreshTokenJpaRepository.save(token);

            }
            else{
                log.info("refreshToken 있음");
                token=findToken.get();
                token.updateRefreshToken(refreshToken);
            }

            jsonResponse.put(JwtTokenUtil.ACCESS_TOKEN, accessToken);
            jsonResponse.put(JwtTokenUtil.REFRESH_TOKEN, refreshToken);

        }
        else{
            log.info("신규 유저");

            jsonResponse.put("nickname", kaKaoInfo.getNickname());
            jsonResponse.put("email", kaKaoInfo.getEmail());
            jsonResponse.put("profileUrl", kaKaoInfo.getProfileUrl());
        }



        response.setContentType("application/json;charset=UTF-8");
        response.setStatus(HttpServletResponse.SC_OK);
        response.getWriter().write(objectMapper.writeValueAsString(jsonResponse));
    }

    private KaKaoInfo getKaKaoInfoFrom(OAuth2User oAuth2User){
        Map<String, String> properties =  oAuth2User.getAttribute("properties");
        Map<String, Object> kakaoAccount = oAuth2User.getAttribute("kakao_account");
        String email= (String) kakaoAccount.get("email");
        String nickname = properties.get("nickname");
        String profileUrl = properties.get("profile_image");

        return KaKaoInfo.builder()
                .email(email)
                .profileUrl(profileUrl)
                .nickname(nickname)
                .build();
    }


}
