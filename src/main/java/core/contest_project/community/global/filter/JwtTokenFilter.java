package core.contest_project.community.global.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

//@Component
@RequiredArgsConstructor
@Slf4j
public class JwtTokenFilter extends OncePerRequestFilter {
    final String BEARER = "Bearer ";
//    private final UserRepositoryImpl userRepository; // 나중에 수정.


    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        /*// header 갖고 옴. 이거 bearer <accessToken> 형식?
        String header = request.getHeader(HttpHeaders.AUTHORIZATION);

        if(header==null || !header.startsWith(BEARER)){
            log.warn("Authorization Header does not start with Bearer");
            filterChain.doFilter(request, response);
            return;
        }

        // 이제 토큰 검증. 1) 만료 2) 진짜 있는 user 인지? <- repository 로?

        String accessToken = header.replace(BEARER, "");

        if(JwtTokenUtil.isTokenExpired(accessToken)){
            log.warn("Token is expired");
            filterChain.doFilter(request, response);
            return;
        }

        String nickname = JwtTokenUtil.getNickname(accessToken);
//        UserDomain userDomain = userRepository.findByNickname(nickname);

//        saveAuthentication(userDomain);

        filterChain.doFilter(request, response);*/
    }

  /*  private void saveAuthentication(UserDomain userDomain){
        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                userDomain, null, null
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);
        log.info("{} 유저 인증 성공", userDomain.nickname());
    }*/
}
