package org.example.wecambackend.repos.meeting;

import org.example.model.meeting.Meeting;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MeetingRepository extends JpaRepository<Meeting, Long> {

    // 특정 학생회의 회의록 목록 조회
    @Query("SELECT m FROM Meeting m " +
           "WHERE m.council.id = :councilId " +
           "ORDER BY m.meetingDateTime DESC")
    List<Meeting> findAllByCouncilIdOrderByMeetingDateTimeDesc(@Param("councilId") Long councilId);

    // 특정 학생회의 회의록 개수 조회
    long countByCouncilId(Long councilId);

    // 회의록 ID와 학생회 ID로 조회 (권한 확인용)
    Optional<Meeting> findByIdAndCouncilId(Long id, Long councilId);

    // 특정 사용자가 생성한 회의록 목록 조회
    @Query("SELECT m FROM Meeting m " +
           "JOIN m.createdBy cm " +
           "WHERE cm.user.id = :userId " +
           "ORDER BY m.createdAt DESC")
    List<Meeting> findAllByCreatedByUserIdOrderByCreatedAtDesc(@Param("userId") Long userId);
}
