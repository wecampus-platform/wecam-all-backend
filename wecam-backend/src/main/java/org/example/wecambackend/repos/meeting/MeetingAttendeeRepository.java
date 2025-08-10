package org.example.wecambackend.repos.meeting;

import org.example.model.meeting.MeetingAttendee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MeetingAttendeeRepository extends JpaRepository<MeetingAttendee, Long> {

    // 특정 회의록의 참석자 목록 조회
    List<MeetingAttendee> findByMeetingIdOrderByCreatedAtAsc(Long meetingId);

    // 특정 회의록의 참석자 개수 조회
    long countByMeetingId(Long meetingId);

    // 특정 회의록의 참석자 삭제
    void deleteByMeetingId(Long meetingId);

    // 특정 회의록과 학생회 멤버로 참석자 조회
    Optional<MeetingAttendee> findByMeetingIdAndCouncilMemberId(Long meetingId, Long councilMemberId);
}
