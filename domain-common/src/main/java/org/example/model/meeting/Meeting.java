package org.example.model.meeting;

import jakarta.persistence.*;
import lombok.*;
import org.example.model.common.BaseEntity;
import org.example.model.council.Council;
import org.example.model.council.CouncilMember;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "meeting")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Meeting extends BaseEntity {

    // 회의록 고유 번호
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "meeting_id")
    private Long id;

    // 회의록 제목
    @Column(name = "title", length = 200, nullable = false)
    private String title;

    // 회의 장소
    @Column(name = "location", length = 100)
    private String location;

    // 회의 일시
    @Column(name = "meeting_datetime", nullable = false)
    private LocalDateTime meetingDateTime;

    // 회의 내용 (마크다운)
    @Column(name = "content", columnDefinition = "MEDIUMTEXT")
    private String content;

    // 회의록 생성자(작성자)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by", nullable = false)
    private CouncilMember createdBy;

    // 소속 학생회
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "council_id", nullable = false)
    private Council council;

    // 만든 템플릿
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "template_id")
    private MeetingTemplate template;

    // 회의 참석자 목록
    @OneToMany(mappedBy = "meeting", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<MeetingAttendee> attendees = new ArrayList<>();

    // 회의록 첨부파일 목록
    @OneToMany(mappedBy = "meeting", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<MeetingFile> files = new ArrayList<>();

    // 편의 메서드
    public void addAttendee(MeetingAttendee attendee) {
        attendees.add(attendee);
        attendee.setMeeting(this);
    }

    public void removeAttendee(MeetingAttendee attendee) {
        attendees.remove(attendee);
        attendee.setMeeting(null);
    }

    public void addFile(MeetingFile file) {
        files.add(file);
        file.setMeeting(this);
    }

    public void removeFile(MeetingFile file) {
        files.remove(file);
        file.setMeeting(null);
    }
}
