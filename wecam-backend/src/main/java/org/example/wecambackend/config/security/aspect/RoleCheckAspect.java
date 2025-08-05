package org.example.wecambackend.config.security.aspect;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.BadRequestException;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.example.wecambackend.common.context.CouncilContextHolder;
import org.example.wecambackend.common.exceptions.BaseException;
import org.example.wecambackend.common.response.BaseResponseStatus;
import org.example.wecambackend.config.security.UserDetailsImpl;
import org.example.model.enums.UserRole;
import org.example.wecambackend.repos.CouncilMemberRepository;
import org.example.wecambackend.util.user.CurrentUserUtil;
import org.springframework.core.annotation.Order;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

/**
 * 인증된 사용자의 역할 및 학생회(council) 관련 권한을 검증하기 위한 Aspect 클래스입니다.
 * 특정 컨트롤러 메서드에 어노테이션(@IsStudent, @IsCouncil, @IsUnauth, @IsUnStudent)을 추가하여,
 * 메서드 호출 전에 역할과 학생회 ID의 유효성을 자동으로 검사할 때 사용합니다.
 */
@Aspect
@Component
@RequiredArgsConstructor
@Order(1)
public class RoleCheckAspect {

    private final CouncilMemberRepository councilMemberRepository;
    private final RedisTemplate<String, String> redisTemplate;

    /**
     * 요청 사용자의 역할이 'STUDENT'인지 확인합니다.
     * @IsStudent 어노테이션이 붙은 메서드 호출 전에 적용됩니다.
     */
    @Before("@annotation(org.example.wecambackend.config.security.annotation.IsStudent)")
    public void checkStudent() {
        checkUserRole(UserRole.STUDENT);
    }

    /**
     * @IsCouncil 어노테이션이 붙은 클래스 또는 메서드 호출 전에 적용되며,
     * 요청 헤더에서 "X-Council-Id" 값을 확인하여, 현재 사용자의 Redis에 저장된 학생회 ID와 일치하는지 검증합니다.
     * 일치하지 않으면 BaseException을 발생시키고, 유효하면 CouncilContextHolder에 councilId를 설정합니다.
     */
    @Before("@within(org.example.wecambackend.config.security.annotation.IsCouncil) || " +
            "@annotation(org.example.wecambackend.config.security.annotation.IsCouncil)")
    public void verifyCouncilConsistency(JoinPoint joinPoint) throws BadRequestException {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
        String councilIdHeader = request.getHeader("X-Council-Id");

        if (councilIdHeader == null) {
            throw new BaseException(BaseResponseStatus.MISSING_COUNCIL_ID_HEADER);
        }

        Long headerCouncilId = Long.valueOf(councilIdHeader);

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (!(authentication.getPrincipal() instanceof UserDetailsImpl userDetails)) {
            throw new BaseException(BaseResponseStatus.NOT_AUTHENTICATED_USER);
        }

        Long userId = userDetails.getId();
        String redisKey = "currentCouncil:" + userId;
        String redisValue = redisTemplate.opsForValue().get(redisKey);

        if (redisValue == null) {
            throw new BaseException(BaseResponseStatus.COUNCIL_MISMATCH);
        }

        Long currentCouncilId = Long.valueOf(redisValue);

        if (!currentCouncilId.equals(headerCouncilId)) {
            throw new BaseException(BaseResponseStatus.COUNCIL_MISMATCH);
        }

        // 검증된 councilId를 컨텍스트 홀더에 설정 (각 요청이 끝난 후 반드시 clear 해야 함)
        CouncilContextHolder.setCouncilId(currentCouncilId);
    }


    /**
     * 요청 사용자의 역할이 'UNAUTH'인지 확인합니다.
     * @IsUnauth 어노테이션이 붙은 메서드 호출 전에 적용됩니다.
     */
    @Before("@annotation(org.example.wecambackend.config.security.annotation.IsUnauth)")
    public void checkUnauth() {
        checkUserRole(UserRole.UNAUTH);
    }

    /**
     * 요청 사용자의 역할이 'GUEST_STUDENT' 또는 'UNAUTH'인지 확인합니다.
     * @IsUnStudent 어노테이션이 붙은 메서드 호출 전에 적용됩니다.
     */
    @Before("@annotation(org.example.wecambackend.config.security.annotation.IsUnStudent)")
    public void checkUnStudent() {
        UserRole role = getCurrentUser().getRole();

        if (role != UserRole.GUEST_STUDENT && role != UserRole.UNAUTH) {
            throw new BaseException(BaseResponseStatus.NO_PERMISSION_TO_MANAGE);
        }
    }

    /**
     * 주어진 역할(requiredRole)이 현재 사용자의 역할과 일치하는지 검사합니다.
     * 일치하지 않으면 BaseException을 발생시킵니다.
     */
    private void checkUserRole(UserRole requiredRole) {
        if (getCurrentUser().getRole() != requiredRole) {
            throw new BaseException(BaseResponseStatus.ROLE_REQUIRED);
        }
    }

    /**
     * 현재 인증된 사용자(UserDetailsImpl)를 반환합니다.
     */
    private UserDetailsImpl getCurrentUser() {
        return CurrentUserUtil.getCurrentUserDetails();
    }
}
