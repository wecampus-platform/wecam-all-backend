package org.example.wecambackend.config.security.aspect;

import lombok.RequiredArgsConstructor;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.reflect.MethodSignature;
import org.example.wecambackend.config.security.UserDetailsImpl;
import org.example.model.enums.UserRole;
import org.example.wecambackend.repos.CouncilMemberRepository;
import org.example.wecambackend.util.CurrentUserUtil;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;

@Aspect
@Component
@RequiredArgsConstructor
public class RoleCheckAspect {

    private final CouncilMemberRepository councilMemberRepository;

    @Before("@annotation(org.example.wecambackend.config.security.annotation.IsStudent)")
    public void checkStudent() {
        checkUserRole(UserRole.STUDENT);
    }

    @Before("@annotation(org.example.wecambackend.config.security.annotation.IsCouncil)")
    public void checkCouncil(JoinPoint joinPoint) {
        UserDetailsImpl currentUser = getCurrentUser();

        if (currentUser.getRole() != UserRole.COUNCIL) {
            throw new AccessDeniedException("학생회 권한이 없습니다.");
        }

        MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
        Method method = methodSignature.getMethod();
        Object[] args = joinPoint.getArgs();

        // 파라미터 이름 추출
        String[] paramNames = methodSignature.getParameterNames();

        Long councilId = null;

        for (int i = 0; i < paramNames.length; i++) {
            if ("councilId".equals(paramNames[i]) && args[i] instanceof Long) {
                councilId = (Long) args[i];
                break;
            }
        }

        if (councilId == null) {
            throw new IllegalArgumentException("councilId 파라미터가 필요합니다.");
        }

        boolean isCouncilMember = councilMemberRepository.existsByUserUserPkIdAndCouncil_IdAndIsActiveTrue(
                currentUser.getId(), councilId);

        if (!isCouncilMember) {
            throw new AccessDeniedException("요청한 학생회에 소속되지 않았습니다.");
        }
    }


    @Before("@annotation(org.example.wecambackend.config.security.annotation.IsUnauth)")
    public void checkUnauth() {
        checkUserRole(UserRole.UNAUTH);
    }

    @Before("@annotation(org.example.wecambackend.config.security.annotation.IsUnStudent)")
    public void checkUnStudent() {
        UserRole role = getCurrentUser().getRole();

        if (role != UserRole.GUEST_STUDENT && role != UserRole.UNAUTH) {
            throw new AccessDeniedException("접근이 불가합니다.");
        }
    }

    private void checkUserRole(UserRole requiredRole) {
        if (getCurrentUser().getRole() != requiredRole) {
            throw new AccessDeniedException(requiredRole.name() + "만 접근할 수 있습니다.");
        }
    }

    private UserDetailsImpl getCurrentUser() {
        return CurrentUserUtil.getCurrentUserDetails();
    }
}
