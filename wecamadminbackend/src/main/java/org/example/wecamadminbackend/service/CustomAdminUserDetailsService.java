package org.example.wecamadminbackend.service;

import lombok.RequiredArgsConstructor;
import org.example.wecamadminbackend.entity.AdminUser;
import org.example.wecamadminbackend.repos.AdminUserRepository;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomAdminUserDetailsService implements UserDetailsService {

    private final AdminUserRepository adminUserRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        AdminUser adminUser = adminUserRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Admin user not found: " + username));
        System.out.println("Loaded user: " + adminUser.getUsername() + ", role: " + adminUser.getRole());

        return User.builder()
                .username(adminUser.getUsername())
                .password(adminUser.getPassword())
                .authorities(new SimpleGrantedAuthority(adminUser.getRole()))
                .build();
    }
}

