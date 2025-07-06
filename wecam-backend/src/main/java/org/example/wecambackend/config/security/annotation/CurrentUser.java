package org.example.wecambackend.config.security.annotation;

import java.lang.annotation.*;


//접속한 유저가 학생회 관리자 페이지의 접근 권한이 있는지 확인 (선택한 학생회 관리자 페이지의 councilId 에 속한 멤버인지 확인)
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface CurrentUser {
}
