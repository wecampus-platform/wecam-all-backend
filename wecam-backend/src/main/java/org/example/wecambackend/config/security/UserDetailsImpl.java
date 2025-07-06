package org.example.wecambackend.config.security;

import lombok.Getter;
import lombok.Setter;
import org.example.model.enums.UserRole;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

//서버 분산 시에는 레디스 로 구현 해야 함.
public class UserDetailsImpl implements UserDetails {

    @Getter
    private final Long id;
    private final String email;
    @Getter
    private final UserRole role;
    @Getter
    private final Long organizationId;

    @Getter
    private final Boolean auth;

    @Getter
    private final List<Long> councilId;

    @Setter @Getter
    private Long currentCouncilId;
    // COUNCIL 역할인 경우에만 사용 -> 여러개일 수 있음 , 비대위장 학생회장 겸임 가능함.


    //로그인 이후 JwtAuthenticationFilter에서 사용자 정보를 기반으로 객체 생성
    public UserDetailsImpl(Long id, String email, UserRole role, Long organizationId, Boolean auth,List<Long> councilId ) {
        this.councilId = councilId;
        this.id = id;
        this.email = email;
        this.role = role;
        this.organizationId = organizationId;
        this.auth = auth;
    }
    //JWT 기반 구조에서는 비밀번호가 필요하지 않기 때문에 null 반환
    @Override
    public String getPassword() {
        return null; // JWT에서는 필요 없음_로그인이 아니니까.
    }

    //Spring Security는 내부적으로 사용자명을 getUsername()을 통해 확인
    @Override public String getUsername() { return email; }


    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_" + role.name()));
    }

    @Override public boolean isAccountNonExpired() { return true; }
    @Override public boolean isAccountNonLocked() { return true; }
    @Override public boolean isCredentialsNonExpired() { return true; }
    @Override public boolean isEnabled() { return true; }

}
