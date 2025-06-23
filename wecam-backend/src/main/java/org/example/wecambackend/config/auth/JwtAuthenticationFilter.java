package org.example.wecambackend.config.auth;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.example.wecambackend.config.security.UserDetailsImpl;
import org.example.model.user.User;
import org.example.wecambackend.repos.UserRepository;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.example.wecambackend.config.security.context.CurrentUserContext;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtTokenProvider jwtTokenProvider;
    private final UserRepository userRepository;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        String authHeader = request.getHeader("Authorization");
        String uri = request.getRequestURI();

        // 여기서 특정 경로는 토큰 검사 제외
        if (uri.equals("/affiliation-test.html")
                || uri.startsWith("/public/")
                || uri.equals("/login_example.html")
                || uri.equals("/affiliation_approve_test.html")
                || uri.equals("/mypage_example.html")
                || uri.equals("/organizationRequest_test.html")
                || uri.startsWith("/css/")
                || uri.startsWith("/js/")
                || uri.startsWith("/images/")) {
            filterChain.doFilter(request, response);
            return;
        }

        System.out.println("[JwtFilter] 요청 URI: " + uri);
        System.out.println("[JwtFilter] Authorization 헤더: " + authHeader);

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);

            if (jwtTokenProvider.validateToken(token)) {
                String email = jwtTokenProvider.getEmailFromToken(token);

                User user = userRepository.findByEmailWithPrivate(email)
                        .orElseThrow(() -> new RuntimeException("인증된 사용자를 찾을 수 없습니다."));

                UserDetailsImpl userDetails = new UserDetailsImpl(
                        user.getUserPkId(),
                        user.getEmail(),
                        user.getRole(),
                        user.getOrganizationId(),
                        user.isAuthentication()
                );

                UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(
                                userDetails,
                                null,
                                userDetails.getAuthorities()
                        );

                authentication.setDetails(
                        new WebAuthenticationDetailsSource().buildDetails(request)
                );

                SecurityContextHolder.getContext().setAuthentication(authentication);
                CurrentUserContext.set(userDetails);
            }
        }

        filterChain.doFilter(request, response);
    }
}
