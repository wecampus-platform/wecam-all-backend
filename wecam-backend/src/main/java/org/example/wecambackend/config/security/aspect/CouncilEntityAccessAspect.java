package org.example.wecambackend.config.security.aspect;


import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.example.wecambackend.common.context.CouncilContextHolder;
import org.example.wecambackend.common.exceptions.BaseException;
import org.example.wecambackend.common.response.BaseResponseStatus;
import org.example.wecambackend.config.security.annotation.CheckCouncilEntity;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;

@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
@Order(2)
public class CouncilEntityAccessAspect {

    private final EntityManager entityManager;

    @Around("@annotation(checkCouncilEntity)")
    public Object validateCouncilEntityAccess(
            ProceedingJoinPoint joinPoint,
            CheckCouncilEntity checkCouncilEntity
    ) throws Throwable {

        // 1. 현재 로그인한 학생회의 ID 가져오기
        Long currentCouncilId = CouncilContextHolder.getCouncilId();

        // 2. 파라미터에서 엔티티 ID (예: invitationId) 꺼내기
        Long entityId = extractLongParam(joinPoint, checkCouncilEntity.idParam());

        // 3. Entity 조회
        Object entity = entityManager.find(checkCouncilEntity.entityClass(), entityId);
        if (entity == null) {
            throw new BaseException(BaseResponseStatus.ENTITY_NOT_FOUND);
        }

        // 4. 해당 Entity의 councilId 가져오기
        Long targetCouncilId = extractCouncilIdFromEntity(entity);

        // 5. 현재 로그인한 학생회의 소속인지 검증
        if (!currentCouncilId.equals(targetCouncilId)) {
            throw new BaseException(BaseResponseStatus.INVALID_COUNCIL_ACCESS);
        }

        return joinPoint.proceed();
    }

    private Long extractLongParam(ProceedingJoinPoint joinPoint, String paramName) {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        String[] paramNames = signature.getParameterNames();
        Object[] args = joinPoint.getArgs();

        for (int i = 0; i < paramNames.length; i++) {
            if (paramNames[i].equals(paramName) && args[i] instanceof Long) {
                return (Long) args[i];
            }
        }

        throw new BaseException(BaseResponseStatus.MISSING_ENTITY_ID_PARAM);
    }

    private Long extractCouncilIdFromEntity(Object entity) {
        try {
            // getCouncil().getId() → Entity가 Council 엔티티를 참조하고 있는 경우
            Method getCouncil = entity.getClass().getMethod("getCouncil");
            Object council = getCouncil.invoke(entity);
            Method getId = council.getClass().getMethod("getId");
            return (Long) getId.invoke(council);

        } catch (NoSuchMethodException e) {
            // getCouncilId() → 단순 Long 타입으로 가지고 있는 경우
            try {
                Method getCouncilId = entity.getClass().getMethod("getCouncilId");
                return (Long) getCouncilId.invoke(entity);
            } catch (Exception e2) {
                throw new BaseException(BaseResponseStatus.COUNCIL_ID_EXTRACTION_FAILED);
            }
        } catch (Exception e) {
            throw new BaseException(BaseResponseStatus.COUNCIL_ID_EXTRACTION_FAILED);
        }
    }
}
