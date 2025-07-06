package org.example.wecambackend.service.admin;

import lombok.extern.slf4j.Slf4j;
import org.example.wecambackend.config.security.UserDetailsImpl;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class AdminAuthService {

    public void setCurrentCouncilId(Long councilId, UserDetailsImpl userDetails){
        userDetails.setCurrentCouncilId(councilId);
    }
}
