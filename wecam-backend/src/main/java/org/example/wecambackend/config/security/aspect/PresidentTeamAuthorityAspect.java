package org.example.wecambackend.config.security.aspect;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.example.model.council.CouncilMember;
import org.example.model.enums.MemberRole;
import org.example.wecambackend.common.context.CouncilContextHolder;
import org.example.wecambackend.common.exceptions.BaseException;
import org.example.wecambackend.common.response.BaseResponseStatus;
import org.example.wecambackend.config.security.UserDetailsImpl;
import org.example.wecambackend.config.security.annotation.IsPresidentTeam;
import org.example.wecambackend.repos.CouncilMemberRepository;
import org.springframework.core.annotation.Order;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 회장단 권한을 검사하는 Aspect
 * 회장(PRESIDENT)과 부회장(VICE_PRESIDENT)만 접근 가능
 */
@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
@Order(3)
public class PresidentTeamAuthorityAspect {

    private final CouncilMemberRepository councilMemberRepository;

    @Around("@annotation(isPresidentTeam)")
    public Object checkPresidentTeamAuthority(
            ProceedingJoinPoint joinPoint,
            IsPresidentTeam isPresidentTeam
    ) throws Throwable {
        
        // 1. 현재 로그인한 사용자 정보 가져오기
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !(authentication.getPrincipal() instanceof UserDetailsImpl)) {
            throw new BaseException(BaseResponseStatus.UNAUTHORIZED);
        }

        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        Long currentUserId = userDetails.getId();
        Long currentCouncilId = CouncilContextHolder.getCouncilId();

        // 2. 현재 사용자가 해당 학생회의 회장 또는 부회장인지 확인
        List<CouncilMember> currentMembers = councilMemberRepository.findByUserUserPkIdAndStatus(currentUserId, org.example.model.common.BaseEntity.Status.ACTIVE);
        CouncilMember currentMember = currentMembers.stream()
                .filter(member -> member.getCouncil().getId().equals(currentCouncilId))
                .findFirst()
                .orElse(null);

        if (currentMember == null) {
            throw new BaseException(BaseResponseStatus.INVALID_COUNCIL_ACCESS);
        }

        MemberRole memberRole = currentMember.getMemberRole();
        if (memberRole != MemberRole.PRESIDENT && memberRole != MemberRole.VICE_PRESIDENT) {
            throw new BaseException(BaseResponseStatus.ROLE_REQUIRED);
        }

        log.info("회장단 권한 검증 통과 - 사용자: {}, 역할: {}", currentUserId, memberRole);
        
        return joinPoint.proceed();
    }
} 