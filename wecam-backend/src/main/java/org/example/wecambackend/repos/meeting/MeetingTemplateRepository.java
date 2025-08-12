package org.example.wecambackend.repos.meeting;

import org.example.model.meeting.MeetingTemplate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MeetingTemplateRepository extends JpaRepository<MeetingTemplate, Long> {
    
    /**
     * 특정 학생회의 템플릿과 전체 공통 템플릿을 조회
     */
    @Query("SELECT mt FROM MeetingTemplate mt WHERE mt.council.id = :councilId OR mt.council IS NULL ORDER BY mt.isDefault DESC, mt.name ASC")
    List<MeetingTemplate> findByCouncilIdOrCommon(@Param("councilId") Long councilId);
}
