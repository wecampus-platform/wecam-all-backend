package org.example.wecambackend.repos.meeting;

import org.example.model.meeting.Meeting;

import java.util.List;

public interface MeetingRepositoryCustom {
    
    /**
     * 필터링과 정렬을 지원하는 회의록 목록 조회
     */
    List<Meeting> findMeetingsWithFilters(
            Long councilId, 
            Long categoryId, 
            Long attendeeId, 
            String sortOrder);
}
