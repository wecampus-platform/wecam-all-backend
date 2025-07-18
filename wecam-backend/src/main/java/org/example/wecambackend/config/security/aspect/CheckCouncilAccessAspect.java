package org.example.wecambackend.config.security.aspect;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.example.wecambackend.common.exceptions.BaseException;
import org.example.wecambackend.common.response.BaseResponseStatus;
import org.example.wecambackend.common.validation.CouncilAccessValidator;
import org.example.wecambackend.config.security.UserDetailsImpl;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

/**
 * 이 Aspect는 @CheckCouncilAccess 어노테이션이 붙은 컨트롤러 메서드 실행 전에 동작함.
 * 역할:
 * 1. 요청 헤더에서 X-Council-Id 값을 추출하고
 * 2. 현재 로그인한 사용자의 권한과 해당 학생회(council)에 대한 접근 권한을 검증하며
 * 3. redis 에 현재 선택된 council ID 를 저장함 (세션 상태처럼 사용).
 */
@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
public class CheckCouncilAccessAspect {

    private final HttpServletRequest request; // 현재 HTTP 요청 객체
    private final CouncilAccessValidator councilAccessValidator; // 실제 검증 로직을 분리한 Validator
    private final RedisTemplate<String, String> redisTemplate; // 현재 사용자의 council 상태를 Redis에 캐시용으로 저장

    /**
     * @CheckCouncilAccess 어노테이션이 붙은 메서드 실행 전에 수행됨
     */
    @Before("@annotation(org.example.wecambackend.config.security.annotation.CheckCouncilAccess)")
    public void validateCouncilAccess() {
        // 1. 인증 정보 가져오기
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        // 인증되지 않은 사용자이거나, 인증 정보가 예상한 타입이 아닌 경우 예외 발생
        if (authentication == null || !(authentication.getPrincipal() instanceof UserDetailsImpl)) {
            throw new RuntimeException("인증된 사용자 정보가 없습니다.");
        }

        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal(); // 현재 로그인한 사용자 정보
        Long userId = userDetails.getId();

        // 2. 요청 헤더에서 X-Council-Id 값을 추출
        String councilIdHeader = request.getHeader("X-Council-Id");
        if (councilIdHeader == null) {
            throw new BaseException(BaseResponseStatus.MISSING_COUNCIL_ID_HEADER);
        }

        // 3. Long으로 파싱 (형식 검증)
        Long councilId;
        try {
            councilId = Long.parseLong(councilIdHeader);
        } catch (NumberFormatException e) {
            throw new BaseException(BaseResponseStatus.MISSING_COUNCIL_ID_HEADER);
        }

        // 4. 실제 유저가 해당 학생회에 속해 있는지 검증
        // → 내부적으로 DB 또는 Redis 등을 조회하여 사용자의 권한 또는 멤버십 확인
        councilAccessValidator.validateMembership(userDetails, councilId);

        // 5. Redis에 현재 사용자의 "선택된 학생회 ID"를 저장
        //    (key = currentCouncil:{userId}, value = councilId)
        redisTemplate.opsForValue().set("currentCouncil:" + userId, String.valueOf(councilId));

        // 이후의 로직 (예: 다른 AOP나 서비스)에서는 이 Redis 값을 기반으로 현재 학생회 ID를 추론 가능
    }
}
