package com.likelion.backendplus4.talkpick.backend.common.configuration.security;

import static org.springframework.security.config.http.SessionCreationPolicy.STATELESS;

import com.likelion.backendplus4.talkpick.backend.auth.infrastructure.security.filter.JwtFilter;
import com.likelion.backendplus4.talkpick.backend.auth.infrastructure.security.custom.handler.CustomAccessDeniedHandler;
import com.likelion.backendplus4.talkpick.backend.auth.infrastructure.security.custom.handler.CustomAuthenticationEntryPoint;
import com.likelion.backendplus4.talkpick.backend.user.infrastructure.adapter.persistence.jpa.entity.Role;

import java.util.Arrays;
import java.util.List;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.header.HeaderWriter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

/**
 * Security 설정을 담당하는 Configuration 클래스입니다.
 *
 * @since 2025-05-12
 * @modified 2025-05-19
 */
@Configuration
public class SecurityConfig {

    private final CustomAccessDeniedHandler customAccessDeniedHandler;
    private final CustomAuthenticationEntryPoint customAuthenticationEntryPoint;
    private final JwtFilter jwtFilter;
    private final String HOST_NAME;
    private final String PUBLIC_CACHE_HEADER;
    private final String CACHE_PREFIX;
    private final String PRAGMA_HEADER;

    public SecurityConfig(
        CustomAccessDeniedHandler customAccessDeniedHandler,
        CustomAuthenticationEntryPoint customAuthenticationEntryPoint,
        JwtFilter jwtFilter,
        @Value("${host.name}") String HOST_NAME,
        @Value("${cache.header}") String PUBLIC_CACHE_HEADER,
        @Value("${cache.prefix}") String CACHE_PREFIX,
        @Value("${cache.pragma}") String PRAGMA_HEADER) {
        this.customAccessDeniedHandler = customAccessDeniedHandler;
        this.customAuthenticationEntryPoint = customAuthenticationEntryPoint;
        this.jwtFilter = jwtFilter;
        this.HOST_NAME = HOST_NAME;
        this.PUBLIC_CACHE_HEADER = PUBLIC_CACHE_HEADER;
        this.CACHE_PREFIX = CACHE_PREFIX;
        this.PRAGMA_HEADER = PRAGMA_HEADER;
    }

    /**
     * AuthenticationManager를 구성합니다.
     * HttpSecurity에서 공유한 AuthenticationManagerBuilder를 사용하여 빌드합니다.
     *
     * @param http HttpSecurity
     * @return AuthenticationManager 객체
     * @author 박찬병
     * @since 2025-05-13
     * @modified 2025-05-13
     */
    @Bean
    public AuthenticationManager authenticationManager(HttpSecurity http) throws Exception {
        AuthenticationManagerBuilder authenticationManagerBuilder = http.getSharedObject(AuthenticationManagerBuilder.class);
        return authenticationManagerBuilder.build();
    }

    /**
     * PasswordEncoder를 구성합니다.
     * DelegatingPasswordEncoder를 사용하여 다양한 인코딩 방식을 지원합니다.
     *
     * @return PasswordEncoder 객체
     * @author 박찬병
     * @since 2025-05-13
     * @modified 2025-05-13
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }

    /**
     * SecurityFilterChain을 구성합니다.
     * - HTTP Basic 및 CSRF 비활성화
     * - CORS 설정 적용
     * - 세션 무상태(STATELESS) 설정
     * - 모든 요청 permitAll
     * - 예외 처리 핸들러 등록
     * - JWT 필터 추가
     *
     * @param http HttpSecurity
     * @return SecurityFilterChain 객체
     * @throws Exception 설정 중 예외 발생 시
     * @author 박찬병
     * @since 2025-05-13
     * @modified 2025-05-29
     * 2025-05-19 - url 설정 추가
     * 2025-05-29 - 캐싱 경로 설정 추가
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        return http
            .httpBasic(AbstractHttpConfigurer::disable)
            .csrf(AbstractHttpConfigurer::disable)
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))
            .sessionManagement(sessionManagement -> sessionManagement.sessionCreationPolicy(
                STATELESS))
            .authorizeHttpRequests(authorizationManagerRequestMatcherRegistry ->
                authorizationManagerRequestMatcherRegistry
                    .requestMatchers("/auth/**").permitAll()
                    .requestMatchers("/public/**").permitAll()
                    .requestMatchers("/public/news/**").authenticated()
                    .requestMatchers("/ws-chat/**").permitAll() // TODO: 웹소켓 인증관련 설정 시 수정
                    .requestMatchers("/user/**").hasRole(Role.USER.getRoleName())
                    .requestMatchers("/admin/**").hasRole(Role.ADMIN.getRoleName())
                    .requestMatchers("/actuator/prometheus", "/actuator/health", "/actuator/metrics").permitAll()
                    .anyRequest().authenticated())
            .exceptionHandling(e -> e
                .authenticationEntryPoint(customAuthenticationEntryPoint)
                .accessDeniedHandler(customAccessDeniedHandler))
            .addFilterBefore(jwtFilter,
                UsernamePasswordAuthenticationFilter.class)
            .headers(headers -> headers
                .addHeaderWriter(publicCacheWriter()))
            .build();
    }

    /**
     * CORS 설정을 구성합니다.
     * Nginx에서 CORS 헤더를 처리하지 않는 경우를 대비해 Spring에서도 설정을 적용합니다.
     * - 허용 Origin: 설정된 HOST_NAME
     * - 허용 HTTP 메서드: HEAD, GET, POST, PUT, DELETE, PATCH
     * - 모든 헤더 허용
     * - 자격 증명 포함 허용 (Allow-Credentials)
     *
     * TODO Nginx에서 cors 설정을 모두 해줄 시 삭제
     * @return CORS 설정 소스
     * @author 박찬병
     * @since 2025-05-13
     * @modified 2025-05-13
     */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(List.of(HOST_NAME));
        configuration.setAllowedMethods(
            Arrays.asList("HEAD", "GET", "POST", "PUT", "DELETE", "PATCH"));
        configuration.setAllowedHeaders(List.of("*"));
        configuration.setAllowCredentials(true);
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    /**
     * 요청 경로가 캐시 대상인 경우 Public Cache-Control 헤더를 설정하는 HeaderWriter를 생성합니다.
     *
     * @return 요청 경로가 캐시 대상인 경우 Public Cache-Control 헤더가 설정된 HeaderWriter 객체
     * @author 박찬병
     * @since 2025-05-29
     */
    private HeaderWriter publicCacheWriter() {
        return (request, response) -> {
            String path = request.getServletPath();
            if (path.startsWith(CACHE_PREFIX)) {
                response.setHeader(
                    HttpHeaders.CACHE_CONTROL,
                    PUBLIC_CACHE_HEADER
                );
                response.setHeader(HttpHeaders.PRAGMA, PRAGMA_HEADER);
            }
        };
    }

}