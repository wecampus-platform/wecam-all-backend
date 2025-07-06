package org.example.wecambackend.config.security.aspect;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.example.wecambackend.config.security.UserDetailsImpl;
import org.example.wecambackend.config.security.annotation.CheckOwner;
import org.example.wecambackend.exception.UnauthorizedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PathVariable;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;

@Aspect
@Component
@RequiredArgsConstructor
public class OwnerCheckAspect {

    private final EntityManager entityManager;

    @Around("@annotation(checkOwner)")
    public Object checkOwnership(ProceedingJoinPoint joinPoint, CheckOwner checkOwner) throws Throwable {
        Long currentUserId = getCurrentUserId();

        // 컨트롤러 파라미터 중 idParam 값 추출
        Long targetId = getPathVariableValue(joinPoint, checkOwner.idParam());

        // 엔티티 조회
        Object entity = entityManager.find(checkOwner.entity(), targetId);
        if (entity == null) {
            throw new EntityNotFoundException("대상을 찾을 수 없습니다.");
        }

        // 작성자 ID 추출
        Long authorId = extractAuthorId(entity, checkOwner.authorGetter());

        // 비교
        if (!authorId.equals(currentUserId)) {
            throw new UnauthorizedException("작성자만 수정할 수 있습니다.");
        }

        return joinPoint.proceed();
    }

    private Long getCurrentUserId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !(auth.getPrincipal() instanceof UserDetailsImpl userDetails)) {
            throw new UnauthorizedException("로그인 정보가 없습니다.");
        }
        return userDetails.getId();
    }

    private Long getPathVariableValue(ProceedingJoinPoint joinPoint, String paramName) {
        Method method = ((MethodSignature) joinPoint.getSignature()).getMethod();
        Object[] args = joinPoint.getArgs();
        Parameter[] parameters = method.getParameters();

        for (int i = 0; i < parameters.length; i++) {
            PathVariable pv = parameters[i].getAnnotation(PathVariable.class);
            if (pv != null && (pv.value().equals(paramName) || parameters[i].getName().equals(paramName))) {
                return Long.valueOf(args[i].toString());
            }
        }
        throw new IllegalArgumentException("PathVariable '" + paramName + "'을 찾을 수 없습니다.");
    }

    private Long extractAuthorId(Object entity, String getterChain) throws Exception {
        String[] methods = getterChain.split("\\.");
        Object current = entity;
        for (String method : methods) {
            current = current.getClass()
                    .getMethod(method)
                    .invoke(current);
        }
        return (Long) current;
    }
}
