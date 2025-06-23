package org.example.wecambackend.config.security.annotation;

import java.lang.annotation.*;

@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface HasAffiliationApprovalAuthority {
    // TODO: 추후 role, permission 등 확장 가능
    //현재는 로직 구현을 안 해둠.
}
