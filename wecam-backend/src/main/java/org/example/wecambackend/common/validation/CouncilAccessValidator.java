package org.example.wecambackend.common.validation;

import lombok.RequiredArgsConstructor;
import org.example.wecambackend.common.exceptions.BaseException;
import org.example.wecambackend.common.response.BaseResponseStatus;
import org.example.wecambackend.config.security.UserDetailsImpl;
import org.springframework.stereotype.Component;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;


//해당 유저가 특정 학생회에 소속되어 있는지 확인
@Component
@RequiredArgsConstructor
public class CouncilAccessValidator {

    /**
     * header로 전달된 councilId가 유효한지 검사
     */
    public void validateMembership(UserDetailsImpl userDetails, Long councilIdFromHeader) {

        if (!userDetails.getCouncilId().contains(councilIdFromHeader)) {
            throw new BaseException(BaseResponseStatus.COUNCIL_ID_EXTRACTION_FAILED);
        }
    }
}
