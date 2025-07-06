package org.example.wecambackend.config.security.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface CheckOwner {
    Class<?> entity();     // 조회할 엔티티 클래스
    String idParam();      // 컨트롤러 파라미터명 (예: "todoId")
    String authorGetter(); // 작성자 ID 조회 메서드명 (예: "getCreateUser.getUserPkId")
}
