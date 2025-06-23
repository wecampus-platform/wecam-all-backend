package org.example.wecambackend.config.security;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.example.wecambackend.config.auth.JwtAuthenticationFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;

import java.util.List;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter; // 생성자 주입

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
        return web -> web.ignoring().requestMatchers(
                "/affiliation_test.html",
                "/login_example.html",
                "/signup_example.html",
                "/affiliation_approve_test.html",
                "/freshman_cert_example.html",
                "/mypage_example.html",
                "/organizationRequest_test.html",
                "/css/**",
                "/js/**",
                "/images/**"
        );
    }


    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class) // 토큰인증
                .csrf(csrf -> csrf.disable())
//                .cors(cors -> cors.configurationSource(request -> {
//                    CorsConfiguration config = new CorsConfiguration();
//                    config.setAllowedOrigins(List.of("http://localhost:3000")); // 프론트 도메인 명시
//                    config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
//                    config.setAllowedHeaders(List.of("*"));
//                    config.setAllowCredentials(true); // ✔ optional
//                    return config;
//                }))
                .cors(cors -> cors.configurationSource(request -> {
                    CorsConfiguration config = new CorsConfiguration();
                    config.setAllowedOriginPatterns(List.of("*")); // 모든 Origin 허용
                    config.setAllowedMethods(List.of("*"));
                    config.setAllowedHeaders(List.of("*"));
                    config.setAllowCredentials(true);
                    return config;
                })) //Test 용 위에껀 프론트 연결용
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(
                                "/",
                                "/swagger-ui/**",
                                "/v3/api-docs/**",
                                "/swagger-resources/**",
                                "/webjars/**",
                                "/public/**",
                                "/auth/check/**",
                                "/client/auth/token/refresh",
                                "/client/auth/logout"
                        ).permitAll()
                        .requestMatchers("/admin/**").hasRole("COUNCIL")  // 특정 권한 필요
                        .anyRequest().authenticated()
                )
                .formLogin(form -> form.disable())
                .exceptionHandling(exception -> exception
                    .authenticationEntryPoint((request, response, authException) -> {
                        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                        response.setContentType("application/json");
                        response.getWriter().write("{\"message\": \"로그인이 필요합니다.\"}");
                    }));
               // Test 용 _ html로 접근되게 함.
//                .exceptionHandling(exception -> exception
//                        .authenticationEntryPoint((request, response, authException) -> {
//                            String acceptHeader = request.getHeader("Accept");
//                            boolean isApiRequest = acceptHeader != null && acceptHeader.contains("application/json");
//
//                            if (isApiRequest || request.getRequestURI().startsWith("/api")) {
//                                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
//                                response.setContentType("application/json");
//                                response.getWriter().write("{\"message\": \"로그인이 필요합니다.\"}");
//                            } else {
//                                response.sendRedirect("/login_example.html");
//                            }
//                        })
//                );


        return http.build();
    }

}
