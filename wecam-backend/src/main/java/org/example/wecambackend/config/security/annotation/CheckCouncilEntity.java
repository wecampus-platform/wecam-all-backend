package org.example.wecambackend.config.security.annotation;

import java.lang.annotation.*;



@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface CheckCouncilEntity {

    /**
     * 리소스를 식별할 파라미터 이름 (예: "invitationId", "todoId")
     */
    String idParam();

    /**
     * 대상 엔티티 클래스 (예: InvitationCode.class, Todo.class 등)
     */
    Class<?> entityClass();
}
