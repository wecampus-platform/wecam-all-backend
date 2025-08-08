package org.example.wecambackend.repos;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.example.model.council.QCouncilMember;
import org.example.model.organization.QOrganization;
import org.example.model.user.QUser;
import org.example.model.user.QUserInformation;
import org.example.model.user.QUserPrivate;
import org.example.model.enums.UserRole;
import org.example.wecambackend.dto.responseDTO.CouncilMemberSearchResponse;
import org.example.wecambackend.dto.responseDTO.StudentSearchResponse;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class UserCustomRepositoryImpl implements UserCustomRepository {
    
    private final JPAQueryFactory queryFactory;
    
    private final QUser user = QUser.user;
    private final QUserInformation userInfo = QUserInformation.userInformation;
    private final QOrganization organization = QOrganization.organization;
    private final QCouncilMember councilMember = QCouncilMember.councilMember;
    
    @Override
    public List<CouncilMemberSearchResponse> searchCouncilMembers(String name) {
        return queryFactory
            .select(Projections.constructor(CouncilMemberSearchResponse.class,
                user.userPkId,
                councilMember.id,
                user.name,
                user.enrollYear,
                organization.organizationName,
                councilMember.department.name,
                councilMember.memberRole.stringValue(),
                userInfo.profileImagePath
            ))
            .from(user)
            .leftJoin(organization).on(user.organizationId.eq(organization.organizationId))
            .leftJoin(councilMember).on(user.userPkId.eq(councilMember.user.userPkId))
            .where(
                user.name.contains(name),
                user.role.eq(UserRole.COUNCIL),
                user.status.eq(org.example.model.common.BaseEntity.Status.ACTIVE)
            )
            .fetch();
    }
    
    @Override
    public List<StudentSearchResponse> searchStudents(String name, List<String> years, List<Integer> grades) {
        BooleanBuilder builder = new BooleanBuilder();
        
        // 기본 조건: 이름 검색 + UNAUTH, ADMIN이 아닌 사용자 + 활성 상태
        builder.and(user.name.contains(name));
        builder.and(user.role.ne(UserRole.ADMIN));
        builder.and(user.role.ne(UserRole.UNAUTH));
        builder.and(user.status.eq(org.example.model.common.BaseEntity.Status.ACTIVE));
        
        // 입학년도 필터
        if (years != null && !years.isEmpty()) {
            builder.and(user.enrollYear.in(years));
        }
        
        // 학년 필터
        if (grades != null && !grades.isEmpty()) {
            builder.and(userInfo.studentGrade.in(grades));
        }
        
        return queryFactory
            .select(Projections.constructor(StudentSearchResponse.class,
                user.userPkId,
                user.name,
                user.enrollYear,
                organization.organizationName,
                userInfo.studentGrade,
                userInfo.academicStatus.stringValue().coalesce("정보 없음"), // 재학 정보 없으면 "정보 없음" 반환
                userInfo.profileImagePath
            ))
            .from(user)
            .leftJoin(userInfo).on(user.userPkId.eq(userInfo.user.userPkId))
            .leftJoin(organization).on(user.organizationId.eq(organization.organizationId))
            .where(builder)
            .fetch();
    }
}
