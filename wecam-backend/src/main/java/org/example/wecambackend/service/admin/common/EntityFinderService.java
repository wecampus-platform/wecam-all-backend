package org.example.wecambackend.service.admin.common;

import lombok.RequiredArgsConstructor;
import org.example.model.council.Council;
import org.example.model.user.User;
import org.example.wecambackend.common.exceptions.BaseException;
import org.example.wecambackend.common.response.BaseResponseStatus;
import org.example.wecambackend.repos.CouncilRepository;
import org.example.wecambackend.repos.UserRepository;
import org.springframework.stereotype.Service;

/**
 * [설명]
 * - 자주 사용되는 엔티티 조회 로직을 공통으로 처리하는 유틸성 서비스입니다.
 * - 조회 실패 시 공통 예외(BaseException)를 던집니다.
 *
 * [사용 예시]
 * - Service 클래스에서 userRepository.findById(...).orElseThrow(...) 대신 사용
 * - 코드 중복 제거 및 예외 메시지 통일 목적
 */
@Service
@RequiredArgsConstructor
public class EntityFinderService {

    private final UserRepository userRepository;
    private final CouncilRepository councilRepository;

    /**
     * [설명]
     * - 사용자 ID로 User 엔티티를 조회합니다.
     * - 존재하지 않으면 ENTITY_NOT_FOUND 예외를 발생시킵니다.
     *
     * @param userId 조회할 사용자 ID
     * @return User 엔티티
     */
    public User getUserByIdOrThrow(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new BaseException(BaseResponseStatus.USER_NOT_FOUND));
    }

    /**
     * [설명]
     * - 학생회 ID로 Council 엔티티를 조회합니다.
     * - 존재하지 않으면 COUNCIL_NOT_FOUND 예외를 발생시킵니다.
     *
     * @param councilId 조회할 학생회 ID
     * @return Council 엔티티
     */
    public Council getCouncilByIdOrThrow(Long councilId) {
        return councilRepository.findById(councilId)
                .orElseThrow(() -> new BaseException(BaseResponseStatus.COUNCIL_NOT_FOUND));
    }
}
