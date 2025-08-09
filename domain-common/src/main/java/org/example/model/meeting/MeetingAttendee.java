package org.example.model.meeting;

import jakarta.persistence.*;
import lombok.*;
import org.example.model.common.BaseEntity;
import org.example.model.council.CouncilMember;
import org.example.model.enums.MeetingAttendanceStatus;
import org.example.model.enums.MeetingRole;

@Entity
@Table(name = "meeting_attendee", uniqueConstraints = {
    @UniqueConstraint(name = "UK_meeting_attendee", columnNames = {"meeting_id", "council_member_id"})
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MeetingAttendee extends BaseEntity {

    // 참석자 고유 번호
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "attendee_id")
    private Long id;

    // 회의록 고유 번호
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "meeting_id", nullable = false)
    private Meeting meeting;

    // 학생회 멤버 고유 번호
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "council_member_id", nullable = false)
    private CouncilMember councilMember;

    // 참석 상태 (PRESENT: 참석, ABSENT: 불참, LATE: 지각)
    @Enumerated(EnumType.STRING)
    @Column(name = "attendance_status")
    @Builder.Default
    private MeetingAttendanceStatus attendanceStatus = MeetingAttendanceStatus.PRESENT;

    // 회의 내 역할 (HOST: 진행자, RECORDER: 기록자, ATTENDEE: 참석자)
    @Enumerated(EnumType.STRING)
    @Column(name = "role")
    @Builder.Default
    private MeetingRole role = MeetingRole.ATTENDEE;
}
