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
import org.example.wecambackend.util.CurrentUserUtil;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;



@Aspect
@Component
@RequiredArgsConstructor
public class RoleCheckAspect {

    private final CouncilMemberRepository councilMemberRepository;

    @Before("@annotation(org.example.wecambackend.config.security.annotation.IsStudent)")
    public void checkStudent() {
        checkUserRole(UserRole.STUDENT);
    }

    private final RedisTemplate<String, String> redisTemplate;

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
            throw new BaseException (BaseResponseStatus.COUNCIL_MISMATCH);
        }

        Long currentCouncilId = Long.valueOf(redisValue);

        if (!currentCouncilId.equals(headerCouncilId)) {
            throw new BaseException(BaseResponseStatus.COUNCIL_MISMATCH);
        }
        CouncilContextHolder.setCouncilId(currentCouncilId); // 무조건 클리어 해줘야된다.

    }

    @Before("@annotation(org.example.wecambackend.config.security.annotation.IsUnauth)")
    public void checkUnauth() {
        checkUserRole(UserRole.UNAUTH);
    }

    @Before("@annotation(org.example.wecambackend.config.security.annotation.IsUnStudent)")
    public void checkUnStudent() {
        UserRole role = getCurrentUser().getRole();

        if (role != UserRole.GUEST_STUDENT && role != UserRole.UNAUTH) {
            throw new BaseException(BaseResponseStatus.NO_PERMISSION_TO_MANAGE);
        }
    }

    private void checkUserRole(UserRole requiredRole) {
        if (getCurrentUser().getRole() != requiredRole) {
            throw new BaseException(BaseResponseStatus.ROLE_REQUIRED);
        }
    }

    private UserDetailsImpl getCurrentUser() {
        return CurrentUserUtil.getCurrentUserDetails();
    }
}
