package org.example.wecambackend.util;

import org.example.wecambackend.config.security.context.CurrentUserContext;
import org.example.wecambackend.config.security.UserDetailsImpl;
import org.example.wecambackend.exception.UnauthorizedException;
import org.example.model.enums.UserRole;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

public class CurrentUserUtil {
    public static UserDetailsImpl getCurrentUserDetails() {
        UserDetailsImpl cached = CurrentUserContext.get();
        if (cached != null) return cached;

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) {
            throw new UnauthorizedException("로그인이 필요합니다.");
        }

        return (UserDetailsImpl) auth.getPrincipal();
    }

    public static Long getUserId() {
        return getCurrentUserDetails().getId();
    }

    public static Long getOrganizationId() {
        return getCurrentUserDetails().getOrganizationId();
    }

    public static UserRole getRole() {
        return getCurrentUserDetails().getRole();
    }
}

