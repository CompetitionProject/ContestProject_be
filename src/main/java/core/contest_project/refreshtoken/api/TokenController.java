package core.contest_project.refreshtoken.api;

import core.contest_project.refreshtoken.common.JwtTokenUtil;
import core.contest_project.refreshtoken.entity.RefreshToken;
import core.contest_project.refreshtoken.service.RefreshTokenService;
import core.contest_project.user.Role;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.security.SignatureException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

import static core.contest_project.refreshtoken.common.JwtTokenUtil.ROLE;
import static core.contest_project.refreshtoken.common.JwtTokenUtil.USER_ID;


@RestController
@RequiredArgsConstructor
@Slf4j
public class TokenController {
    private final RefreshTokenService refreshTokenService;

    /**
     *
     * @param refreshToken
     * @return
     *
     * @apiNote
     *
     * 근데 이거 refresh token 만 넘기면 되는 거야?
     * access token 같이 넘겨서 만료된 거 맞는지 확인해야 하나?
     */

    @PostMapping("/api/token-reissue")
    public ResponseEntity<Map> reissueToken(@CookieValue("refreshToken")String refreshToken){
        log.info("[TokenController][reissueToken]");
        // 임시로 map 이요하자
        if(refreshToken == null){
            log.info("refreshToken is null");
            Map<String, String> response = new HashMap<>();
            response.put("message", "refreshToken is null");
            return ResponseEntity.ok().body(response);
        }
        Map<String, String> jsonResponse = new ConcurrentHashMap<>();
        try{
            log.info("refreshToken= {}", refreshToken);
            Claims claims = JwtTokenUtil.extractAllClaims(refreshToken);

            String tokenType = JwtTokenUtil.getTokenType(claims);

            // 뭔가 있지만 refreshToken 아님.
            if(!Objects.equals(tokenType, JwtTokenUtil.REFRESH_TOKEN)){
                log.warn("token is not access token");
                jsonResponse.put("msg", "token is not access token.");

                return ResponseEntity.ok(jsonResponse);

            }


            log.info("토큰이 정상입니다.");

            Long  userId = claims.get(USER_ID, Long.class);
            String stringRole= claims.get(ROLE, String.class);
            Role role = Role.valueOf(stringRole);

            RefreshToken findToken = refreshTokenService.findByUserIdAndRefreshToken(userId, refreshToken);

            String generatedAccessToken = JwtTokenUtil.generateAccessToken(userId, role);
            String generatedRefreshToken = JwtTokenUtil.generateRefreshToken(userId);

            findToken.updateRefreshToken(generatedRefreshToken);

            refreshTokenService.save(findToken);

            jsonResponse.put(JwtTokenUtil.ACCESS_TOKEN, generatedAccessToken);
            jsonResponse.put(JwtTokenUtil.REFRESH_TOKEN, generatedRefreshToken);


        }catch (SignatureException e){
            // 서명 이상함
            log.info("서명 이상함.");
            jsonResponse.put("msg", "Invalid JWT signature. Please login again.");
        }catch(ExpiredJwtException e){
            // 토큰 만료기간 지남.
            log.info("Token is expired");
            jsonResponse.put("msg", "Token has expired. Please login again.");

        }catch (MalformedJwtException e){
            // 토큰 형식 이상함
            log.info("Malformed JWT.");
            jsonResponse.put("msg", "Malformed JWT token. Please login again.");

        }

        return ResponseEntity.ok(jsonResponse);
    }


    @PostMapping("/test/generate-token")
    public ResponseEntity<Map> generateToken(@RequestParam("userId") Long userId,
                                             @RequestParam("role")Role role){
        log.info("[TokenController][generateToken]");

        String accessToken = JwtTokenUtil.generateAccessToken(userId, role);
        Map<String, String> response = new HashMap<>();
        response.put(JwtTokenUtil.ACCESS_TOKEN, accessToken);
        return ResponseEntity.ok(response);

    }

    @GetMapping("/test/sing-token")
    public ResponseEntity<Map> generateSignToken(){
        log.info("[TokenController][generateSignToken]");


        String signToken = JwtTokenUtil.generateSignToken();
        Map<String, String> response = new HashMap<>();
        response.put(JwtTokenUtil.SIGN_TOKEN, signToken);
        return ResponseEntity.ok(response);
    }
}
