package core.contest_project.refreshtoken.common;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import lombok.Getter;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;

@Getter
@Component
public class JwtTokenUtil {

    private static final String secretKey="core-project-double-dragon-tiger";
    private static final Long accessTokenExpiredTimeMs=1800000L;
    private static final Long refreshTokenExpiredTimeMs= 604800000L;
    public static final String TOKEN_TYPE="token_type";
    public static final String ACCESS_TOKEN="access_token";
    public static final String SIGN_TOKEN="sign_token";
    public static final String REFRESH_TOKEN="refresh_token";
    public static final String USER_ID="user_id";
    public static final String IS_GUEST="is_guest";

    public static String generateSignToken(){
        Claims claims = Jwts.claims();
        claims.put(TOKEN_TYPE, SIGN_TOKEN);

        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + accessTokenExpiredTimeMs))
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    public static String generateAccessToken(Long userId) {
        Claims claims = Jwts.claims();
        claims.put(TOKEN_TYPE, ACCESS_TOKEN);
        claims.put(USER_ID, userId);

        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + accessTokenExpiredTimeMs))
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    public static String generateRefreshToken(Long userId) {
        Claims claims = Jwts.claims();
        claims.put(TOKEN_TYPE, REFRESH_TOKEN);
        claims.put(USER_ID, userId);

        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + refreshTokenExpiredTimeMs))
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }





    public static String getTokenType(String token){
        return extractAllClaims(token).get(TOKEN_TYPE, String.class);
    }
    public static String getTokenType(Claims claims){
        return claims.get(TOKEN_TYPE, String.class);
    }

    public static Boolean isTokenExpired(String token) {
        Date expiration = extractAllClaims(token).getExpiration();
        return expiration.before(new Date());
    }

    public static Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    private static Key getSigningKey() {
        byte[] keyBytes = secretKey.getBytes(StandardCharsets.UTF_8);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}
