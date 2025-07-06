package org.example.wecambackend.config.security.filter;


import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.example.wecambackend.common.context.CouncilContextHolder;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@Order(Ordered.HIGHEST_PRECEDENCE + 11)  // CurrentUserContextCleanupFilter 다음에 실행되게
public class CouncilContextClearFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {
        try {
            // 실제 요청 처리
            filterChain.doFilter(request, response);
        } finally {
            // 요청 종료 후 항상 clear
            CouncilContextHolder.clear();
        }
    }
}
