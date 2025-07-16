package org.example.wecambackend.config.security.aspect;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.example.wecambackend.common.exceptions.BaseException;
import org.example.wecambackend.common.response.BaseResponseStatus;
import org.example.wecambackend.config.security.UserDetailsImpl;
import org.example.wecambackend.config.security.annotation.CheckOwner;
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

    /**
     * @CheckOwner 어노테이션이 붙은 컨트롤러 메서드에 대해, 메서드 실행 전/후로 실행되는 AOP 로직.
     *
     * 기능 요약:
     * - 현재 로그인된 유저의 ID를 가져옴
     * - @PathVariable에서 idParam 이름으로 전달된 엔티티 ID를 가져옴
     * - EntityManager를 통해 해당 엔티티를 DB에서 조회
     * - 엔티티의 작성자(또는 소유자) ID를 getter 메서드 체인을 통해 추출
     * - 현재 사용자와 작성자 ID를 비교해 불일치 시 예외 발생
     */
    @Around("@annotation(checkOwner)")
    public Object checkOwnership(ProceedingJoinPoint joinPoint, CheckOwner checkOwner) throws Throwable {
        Long currentUserId = getCurrentUserId(); // 현재 로그인한 사용자 ID

        // 컨트롤러 메서드의 파라미터에서 특정 PathVariable(idParam)을 추출
        Long targetId = getPathVariableValue(joinPoint, checkOwner.idParam());

        // 해당 ID로 엔티티 조회 (예: Post.class, id=3)
        Object entity = entityManager.find(checkOwner.entity(), targetId);
        if (entity == null) {
            throw new BaseException(BaseResponseStatus.ENTITY_NOT_FOUND); // 예: 존재하지 않는 게시글
        }

        // 엔티티에서 작성자 ID 추출 (예: getAuthor().getId() 식의 getter chain)
        Long authorId = extractAuthorId(entity, checkOwner.authorGetter());

        // 현재 사용자와 비교
        if (!authorId.equals(currentUserId)) {
            throw new BaseException(BaseResponseStatus.ONLY_AUTHOR_CAN_MODIFY); // 작성자 아님
        }

        // 모든 검증 통과 시 원래 메서드 실행
        return joinPoint.proceed();
    }

    /**
     * 현재 로그인된 사용자의 ID를 SecurityContextHolder에서 추출
     */
    private Long getCurrentUserId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !(auth.getPrincipal() instanceof UserDetailsImpl userDetails)) {
            throw new BaseException(BaseResponseStatus.ACCESS_DENIED);
        }
        return userDetails.getId();
    }

    /**
     * 컨트롤러 메서드의 파라미터 중 @PathVariable(name = idParam)인 값을 찾아 반환
     * 예: @PathVariable("postId") Long id → "postId"를 찾아 해당 값 반환
     */
    private Long getPathVariableValue(ProceedingJoinPoint joinPoint, String paramName) {
        Method method = ((MethodSignature) joinPoint.getSignature()).getMethod();
        Object[] args = joinPoint.getArgs();
        Parameter[] parameters = method.getParameters();

        for (int i = 0; i < parameters.length; i++) {
            PathVariable pv = parameters[i].getAnnotation(PathVariable.class);
            // 이름이 일치하거나, value 생략한 경우 파라미터명 비교
            if (pv != null && (pv.value().equals(paramName) || parameters[i].getName().equals(paramName))) {
                return Long.valueOf(args[i].toString());
            }
        }

        throw new BaseException(BaseResponseStatus.PATH_VARIABLE_NOT_FOUND);
    }

    /**
     * 엔티티에서 author ID를 추출하기 위한 메서드 체인 실행
     * 예: "getAuthor.getId" → entity.getAuthor().getId() 호출
     */
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
