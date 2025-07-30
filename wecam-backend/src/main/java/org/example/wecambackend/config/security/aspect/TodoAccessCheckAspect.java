package org.example.wecambackend.config.security.aspect;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.example.model.todo.Todo;
import org.example.wecambackend.config.security.UserDetailsImpl;
import org.example.wecambackend.config.security.annotation.CheckTodoAccess;
import org.example.wecambackend.repos.TodoRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
public class TodoAccessCheckAspect {

    private final TodoRepository todoRepository;

    @Around("@annotation(checkTodoAccess)")
    public Object checkAccess(ProceedingJoinPoint joinPoint, CheckTodoAccess checkTodoAccess) throws Throwable {
        Long todoId = getParamValue(joinPoint, checkTodoAccess.idParam());

        // ★ fetch join으로 매니저와 유저까지 모두 로딩
        Todo todo = todoRepository.findWithManagersAndUsersById(todoId)
                .orElseThrow(() -> new IllegalArgumentException("할 일을 찾을 수 없습니다."));

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        Long currentUserId = userDetails.getId();

        boolean isAuthor = todo.getCreateUser().getUserPkId().equals(currentUserId);
        boolean isManager = todo.getManagers().stream()
                .anyMatch(m -> m.getUser().getUserPkId().equals(currentUserId));

        if (isAuthor || isManager) return joinPoint.proceed();
        else throw new SecurityException("접근 권한이 없습니다.");
    }

    // 파라미터 이름으로 값 꺼내기
    private Long getParamValue(ProceedingJoinPoint joinPoint, String paramName) {
        var method = ((org.aspectj.lang.reflect.MethodSignature) joinPoint.getSignature()).getMethod();
        var paramNames = ((org.aspectj.lang.reflect.MethodSignature) joinPoint.getSignature()).getParameterNames();
        var args = joinPoint.getArgs();

        for (int i = 0; i < paramNames.length; i++) {
            if (paramNames[i].equals(paramName)) {
                return (Long) args[i];
            }
        }

        throw new IllegalArgumentException("파라미터 '" + paramName + "'를 찾을 수 없습니다.");
    }
}
