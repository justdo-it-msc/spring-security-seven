package com.example.spring_security_seven.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.access.hierarchicalroles.RoleHierarchy;
import org.springframework.security.access.hierarchicalroles.RoleHierarchyImpl;
import org.springframework.security.authorization.AuthorizationDecision;
import org.springframework.security.authorization.AuthorizationManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.intercept.RequestAuthorizationContext;

@Configuration
public class SecurityConfig {

    @Value("${security.remember-me.key}")
    private String rememberMeKey;

    /// 비밀번호 암호화용 Bean
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /// Role Hierarchy 설정
    @Bean
    public RoleHierarchy roleHierarchy() {

        return RoleHierarchyImpl.withRolePrefix("ROLE_")
                .role("ADMIN").implies("USER")
                .build();
    }

    /// 시큐리티 필터 구획을 내 마음대로 커스텀
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) {

        // 특정 경로 csrf disable
        http
                .csrf(csrf -> csrf
                        .ignoringRequestMatchers("/logout"));

        // 로그인 필터 설정
        http
                .formLogin(login -> login
                        .loginProcessingUrl("/login")
                        .loginPage("/login"));

        // remember me 설정
        http
                .rememberMe(me -> me
                        .key(rememberMeKey)
                        .rememberMeParameter("remember-me")
                        .tokenValiditySeconds(14 * 24 * 60 * 60));

        // 인가 필터 설정
        http
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/").permitAll()
                        .requestMatchers("/join").permitAll()
                        .requestMatchers("/login").permitAll()
                        .requestMatchers("/user").hasAnyRole("USER", "ADMIN")
                        .requestMatchers("/admin").access(customAuthorizationManager())
                        .anyRequest().denyAll()
                );

        // 세션 고정 보호 (STATE)
        http
                .sessionManagement(session -> session
                        .sessionFixation().changeSessionId());

        // 최종 빌드
        return http.build();
    }

    private AuthorizationManager<RequestAuthorizationContext> customAuthorizationManager() {

        return (authentication, context) -> {

            boolean allowed =
                    authentication.get().getAuthorities().stream()
                            .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));

            return new AuthorizationDecision(allowed);
        };
    }
}