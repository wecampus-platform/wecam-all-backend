package org.example.wecambackend.service.util;

import lombok.RequiredArgsConstructor;
import org.example.wecambackend.common.exceptions.BaseException;
import org.example.wecambackend.common.response.BaseResponseStatus;
import org.example.wecambackend.repos.UserRepository;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserTagGenerator {

    private final UserRepository userRepository;

    public String generateUserTag(Long selectSchoolId, String name) {
        String prefix = selectSchoolId.toString().substring(selectSchoolId.toString().length() - 1);

        for (int i = 0; i < 10; i++) { // 최대 10회 재시도
            int random = (int) (Math.random() * 1000);
            String suffix = String.format("%03d", random);
            String userTag = prefix + suffix;

            boolean exists = userRepository.existsByNameAndUserTag(name, userTag);
            if (!exists) return userTag;
        }

        throw new BaseException(BaseResponseStatus.USER_TAG_GENERATION_FAILED);
    }
}
