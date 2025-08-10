package org.example.wecambackend.repos.user;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.example.model.organization.QOrganization;
import org.example.model.user.QUser;
import org.example.model.user.QUserInformation;
import org.example.model.user.QUserPrivate;
import org.example.model.enums.UserRole;
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
    
    @Override
    public List<StudentSearchResponse> searchStudents(String name, List<String> years, List<Integer> grades) {
        BooleanBuilder builder = new BooleanBuilder();
        
        // 기본 조건: 이름 검색 + UNAUTH, ADMIN이 아닌 사용자 + 활성 상태
        builder.and(user.name.contains(name));
        builder.and(user.role.ne(UserRole.ADMIN));
        builder.and(user.role.ne(UserRole.UNAUTH));
        builder.and(user.status.eq(org.example.model.common.BaseEntity.Status.ACTIVE));
        
        // 입학년도 필터 (2019년은 이전 연도까지 포함, 나머지는 정확히 일치)
        if (years != null && !years.isEmpty()) {
            BooleanBuilder yearBuilder = new BooleanBuilder();
            for (String year : years) {
                if ("2019".equals(year)) {
                    // 2019년 이전까지 포함
                    yearBuilder.or(user.enrollYear.loe(year));
                } else {
                    // 정확히 일치하는 연도만
                    yearBuilder.or(user.enrollYear.eq(year));
                }
            }
            builder.and(yearBuilder);
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
