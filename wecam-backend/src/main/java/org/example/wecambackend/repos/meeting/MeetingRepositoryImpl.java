package org.example.wecambackend.repos.meeting;

import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.example.model.category.QCategoryAssignment;
import org.example.model.meeting.Meeting;
import org.example.model.meeting.QMeeting;
import org.example.model.meeting.QMeetingAttendee;
import org.example.model.common.BaseEntity;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class MeetingRepositoryImpl implements MeetingRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public List<Meeting> findMeetingsWithFilters(
            Long councilId, 
            Long categoryId, 
            Long attendeeId, 
            String sortOrder) {

        QMeeting meeting = QMeeting.meeting;
        QMeetingAttendee attendee = QMeetingAttendee.meetingAttendee;
        QCategoryAssignment categoryAssignment = QCategoryAssignment.categoryAssignment;

        // 기본 쿼리 생성
        JPAQuery<Meeting> query = queryFactory
                .selectDistinct(meeting)
                .from(meeting);

        // 카테고리 필터링이 있는 경우 JOIN
        if (categoryId != null) {
            query.leftJoin(categoryAssignment)
                    .on(categoryAssignment.entityType.eq(org.example.model.category.CategoryAssignment.EntityType.MEETING)
                            .and(categoryAssignment.entityId.eq(meeting.id))
                            .and(categoryAssignment.status.eq(BaseEntity.Status.ACTIVE)));
        }

        // 참석자 필터링이 있는 경우 JOIN
        if (attendeeId != null) {
            query.leftJoin(attendee)
                    .on(attendee.meeting.eq(meeting)
                            .and(attendee.status.eq(BaseEntity.Status.ACTIVE)));
        }

        // WHERE 조건 구성
        BooleanExpression whereClause = meeting.council.id.eq(councilId)
                .and(meeting.status.eq(BaseEntity.Status.ACTIVE));

        if (categoryId != null) {
            whereClause = whereClause.and(categoryAssignment.category.id.eq(categoryId));
        }

        if (attendeeId != null) {
            whereClause = whereClause.and(attendee.councilMember.id.eq(attendeeId));
        }

        query.where(whereClause);

        // 정렬 조건
        OrderSpecifier<?> orderSpecifier;
        if ("OLDEST".equals(sortOrder)) {
            orderSpecifier = meeting.meetingDateTime.asc();
        } else {
            // 기본값: 최신순
            orderSpecifier = meeting.meetingDateTime.desc();
        }

        query.orderBy(orderSpecifier);

        // 모든 데이터 반환
        return query.fetch();
    }
}
