package org.example.wecambackend.config.security.annotation;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import java.lang.annotation.*;

/**
 * 회장단 권한을 검사하는 어노테이션
 * 회장(PRESIDENT)과 부회장(VICE_PRESIDENT)만 접근 가능
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@SecurityRequirement(name = "회장단 권한 필요")
public @interface IsPresidentTeam {
} 