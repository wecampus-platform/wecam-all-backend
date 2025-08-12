package org.example.wecambackend.repos.meeting;

import org.example.model.meeting.Meeting;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MeetingRepository extends JpaRepository<Meeting, Long>, MeetingRepositoryCustom {

    // 특정 학생회의 회의록 목록 조회
    @Query("SELECT m FROM Meeting m " +
           "WHERE m.council.id = :councilId " +
           "ORDER BY m.meetingDateTime DESC")
    List<Meeting> findAllByCouncilIdOrderByMeetingDateTimeDesc(@Param("councilId") Long councilId);
}
