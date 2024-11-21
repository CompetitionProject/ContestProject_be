package core.contest_project.common.config;

import com.fasterxml.jackson.databind.ObjectMapper;

import core.contest_project.common.security.CustomAuthenticationEntryPoint;
import core.contest_project.common.security.CustomAuthenticationSuccessHandler;
import core.contest_project.global.filter.JwtTokenFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AnonymousAuthenticationFilter;
import org.springframework.web.servlet.HandlerExceptionResolver;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SecurityConfig {
    private final CustomAuthenticationSuccessHandler customAuthenticationSuccessHandler;
    private final JwtTokenFilter jwtTokenFilter;

    private static final String[] SWAGGER_URIS = {
            /* swagger v2 */
            "/v2/api-docs",
            "/swagger-resources",
            "/swagger-resources/**",
            "/configuration/ui",
            "/configuration/security",
            "/swagger-ui.html",
            "/webjars/**",
            /* swagger v3 */
            "/v3/api-docs/**",
            "/swagger-ui/**",
            "/swagger-ui/index.html"
    };

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http, HandlerExceptionResolver handlerExceptionResolver) throws Exception {
        http
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(authorizeRequests -> authorizeRequests
                        .requestMatchers(HttpMethod.GET, "/oauth2/authorization/kakao", "/test/**").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/token-reissue", "/api/users/signup", "/test/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/departments", "/api/schools").permitAll()
                                .requestMatchers(HttpMethod.GET, "/test/sing-token").permitAll()
                        .requestMatchers(SWAGGER_URIS).permitAll()

//                        .requestMatchers(HttpMethod.POST, "/api/contests").hasRole("ADMIN")
//                        .requestMatchers(HttpMethod.PUT, "/api/contests/**").hasRole("ADMIN")
//                        .requestMatchers(HttpMethod.DELETE, "/api/contests/**").hasRole("ADMIN")

                        .anyRequest().authenticated()
                )

                .oauth2Login(oauth2Login -> oauth2Login
                        .successHandler(customAuthenticationSuccessHandler)

                )
                .exceptionHandling(httpSecurityExceptionHandlingConfigurer -> httpSecurityExceptionHandlingConfigurer
                        .authenticationEntryPoint(new CustomAuthenticationEntryPoint(new ObjectMapper()))
                )
                .addFilterBefore(jwtTokenFilter, AnonymousAuthenticationFilter.class)

        ;


        return http.build();
    }

}
