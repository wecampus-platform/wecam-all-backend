package org.example.wecambackend.repos.meeting;

import org.example.model.meeting.MeetingFile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MeetingFileRepository extends JpaRepository<MeetingFile, Long> {

    // 특정 회의록의 첨부파일 목록 조회
    List<MeetingFile> findByMeetingIdOrderByCreatedAtAsc(Long meetingId);

    // 특정 회의록의 첨부파일 개수 조회
    long countByMeetingId(Long meetingId);

    // 특정 회의록의 첨부파일 삭제
    void deleteByMeetingId(Long meetingId);
}
