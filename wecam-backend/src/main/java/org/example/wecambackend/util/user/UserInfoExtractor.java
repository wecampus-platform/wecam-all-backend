package org.example.wecambackend.util.user;

import org.example.model.user.User;

/**
 * User 엔티티에서 대표자 정보 추출 유틸리티
 */
public class UserInfoExtractor {

    /**
     * 대표자 이름 추출
     */
    public static String getRepresentativeName(User user) {
        return user != null ? user.getName() : null;
    }

    /**
     * 대표자 프로필 이미지 경로 추출
     */
    public static String getRepresentativeProfileImage(User user) {
        return (user != null && user.getUserInformation() != null)
                ? user.getUserInformation().getProfileImagePath()
                : null;
    }

    /**
     * 대표자 전화번호 추출
     */
    public static String getRepresentativePhoneNumber(User user) {
        return (user != null && user.getUserPrivate() != null)
                ? user.getUserPrivate().getPhoneNumber()
                : null;
    }
}