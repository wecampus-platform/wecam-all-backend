package org.example.wecambackend.config.security.aspect;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.example.wecambackend.common.validation.CouncilAccessValidator;
import org.example.wecambackend.config.security.UserDetailsImpl;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
public class CheckCouncilAccessAspect {

    private final HttpServletRequest request;
    private final CouncilAccessValidator councilAccessValidator;

    private final RedisTemplate<String, String> redisTemplate;


    @Before("@annotation(org.example.wecambackend.config.security.annotation.CheckCouncilAccess)")
    public void validateCouncilAccess() {
        // 1. 인증 정보 가져오기
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !(authentication.getPrincipal() instanceof UserDetailsImpl)) {
            throw new RuntimeException("인증된 사용자 정보가 없습니다.");
        }

        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();

        // 2. 헤더에서 X-Council-Id 추출
        String councilIdHeader = request.getHeader("X-Council-Id");
        if (councilIdHeader == null) {
            throw new RuntimeException("X-Council-Id 헤더가 없습니다.");
        }

        Long councilId;
        try {
            councilId = Long.parseLong(councilIdHeader);
        } catch (NumberFormatException e) {
            throw new RuntimeException("X-Council-Id 형식이 잘못되었습니다.");
        }

        // 3. 공통 validator로 검증
        councilAccessValidator.validateMembership(userDetails, councilId);

        redisTemplate.opsForValue().set("currentCouncil:" + userDetails.getId(), String.valueOf(councilId));

    }
}
