package org.example.wecambackend.config.security.annotation;

import org.springframework.security.access.prepost.PreAuthorize;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


// 접속한 유저의 currentCouncilId 와 프론트에서 전달받은 CouncilId 가 동일한지 확인
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.METHOD})
@PreAuthorize("isAuthenticated()")
public @interface IsCouncil {
}
