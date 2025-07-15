package org.example.wecambackend.util;

import org.example.wecambackend.common.exceptions.BaseException;
import org.example.wecambackend.common.response.BaseResponseStatus;
import org.example.wecambackend.config.security.context.CurrentUserContext;
import org.example.wecambackend.config.security.UserDetailsImpl;
import org.example.model.enums.UserRole;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

public class CurrentUserUtil {
    public static UserDetailsImpl getCurrentUserDetails() {
        UserDetailsImpl cached = CurrentUserContext.get();
        if (cached != null) return cached;

        // 인증 정보가 없거나 인증되지 않은 경우 예외를 던집니다.
        // 로그인하지 않은 사용자가 인증이 필요한 API에 접근할 때 사용됩니다.
        // BaseResponseStatus.UNAUTHORIZED 또는 LOGIN_REQUIRED 중 프로젝트 정책에 따라 선택합니다.
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) {
            throw new BaseException(BaseResponseStatus.UNAUTHORIZED);
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

