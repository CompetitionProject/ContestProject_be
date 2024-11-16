package core.contest_project.global.filter;

import core.contest_project.refreshtoken.common.JwtTokenUtil;
import core.contest_project.user.service.data.UserDomain;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.security.SignatureException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Objects;

import static core.contest_project.refreshtoken.common.JwtTokenUtil.USER_ID;


@Component
@RequiredArgsConstructor
@Slf4j
public class JwtTokenFilter extends OncePerRequestFilter {

    final String BEARER = "Bearer ";

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        String requestURI = request.getRequestURI();


        // /api/token-reissue 경로도 필터 제외
        return "/api/token-reissue".equals(requestURI);
    }

    /**
     *
     * @param request
     * @param response
     * @param filterChain
     * @throws ServletException
     * @throws IOException
     *
     * doFilter: 1) 로그인이 필요한 경우 2) 인증이 성공한 경우.
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String header = request.getHeader(HttpHeaders.AUTHORIZATION);
        String requestURI = request.getRequestURI();
        log.info("[JwtTokenFilter][doFilterInternal]");
        log.info("requestURI= {}", requestURI);


        if (header == null || !header.startsWith(BEARER)) {
            // 로그인이 필요한 경우.
            log.warn("Authorization Header does not start with Bearer");
            filterChain.doFilter(request, response);
            return;
        }

        String token = header.replace(BEARER, "");

        try{
            Claims claims = JwtTokenUtil.extractAllClaims(token);
            log.info("토큰이 정상입니다.");
            String tokenType = JwtTokenUtil.getTokenType(claims);


            if(tokenType.equals(JwtTokenUtil.SIGN_TOKEN)){
                log.info("token is sign_token");
                filterChain.doFilter(request, response);
            }
            else if(tokenType.equals(JwtTokenUtil.ACCESS_TOKEN)){
                Long userId = claims.get(USER_ID, Long.class);
                log.info("userId= {}", userId);
                UserDomain user = UserDomain.builder()
                        .id(userId)
                        .build();

                saveAuthentication(user);

                filterChain.doFilter(request, response);
            }
            else{
                // 다시 로그인 시키자.
                log.warn("token is not access token");
                filterChain.doFilter(request, response);
                return;
            }






        }catch (SignatureException e){
            // 서명 이상함
            log.info("서명 이상함.");
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("Invalid JWT signature. Please login again.");

        }catch(ExpiredJwtException e){
            // 토큰 만료기간 지남.
            log.info("Token is expired");
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("Token has expired. Please login again.");

        }catch (MalformedJwtException e){
            // 토큰 형식 이상함
            log.info("Malformed JWT.");
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write("Malformed JWT token. Please login again.");
        }


    }

    private void saveAuthentication(UserDomain user) {

        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                user, null,
                null
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);
    }
}
