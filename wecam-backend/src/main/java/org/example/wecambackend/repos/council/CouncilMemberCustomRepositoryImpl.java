package org.example.wecambackend.repos.council;

import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.example.model.council.QCouncilMember;
import org.example.model.organization.QOrganization;
import org.example.model.user.QUser;
import org.example.model.user.QUserInformation;
import org.example.model.enums.UserRole;
import org.example.wecambackend.dto.response.councilMember.CouncilMemberSearchResponse;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class CouncilMemberCustomRepositoryImpl implements CouncilMemberCustomRepository {
    
    private final JPAQueryFactory queryFactory;
    
    private final QUser user = QUser.user;
    private final QUserInformation userInfo = QUserInformation.userInformation;
    private final QOrganization organization = QOrganization.organization;
    private final QCouncilMember councilMember = QCouncilMember.councilMember;
    
    @Override
    public List<CouncilMemberSearchResponse> searchCouncilMembers(String name, Long councilId) {
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
                councilMember.council.id.eq(councilId),
                user.name.contains(name),
                user.role.eq(UserRole.COUNCIL),
                user.status.eq(org.example.model.common.BaseEntity.Status.ACTIVE)
            )
            .fetch();
    }
}
