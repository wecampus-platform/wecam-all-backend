package org.example.model.invitation;


import jakarta.persistence.*;
import lombok.*;
import org.example.model.Council;
import org.example.model.organization.Organization;
import org.example.model.common.BaseTimeEntity;
import org.example.model.enums.CodeType;

import org.example.model.user.User;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "invitation_code")
public class InvitationCode extends BaseTimeEntity {

    //초대코드 pk키
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "invitation_pk_id")
    private Long id;

    //초대코드를 발급한 학생회
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "council_id", nullable = false)
    private Council council;

    //초대코드를 만든 사람
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_pk_id")
    private User user;

    //코드
    @Column(name="code",nullable = false,length = 20,unique = true)
    private String code;

    //사용횟수
    @Column(name="usage_count",nullable = false)
    private int usageCount = 0;

    //초대코드를 발급한 조직
    @ManyToOne
    @JoinColumn(name = "organization_id",nullable = false)
    private Organization organization;

    //council_member,student_member
    @Enumerated(EnumType.STRING)
    @Column(name = "code_type",nullable = false)
    private CodeType codeType;


    //isUsageLimit 이 True 면 값이 들어와야 함.
    @Column(name = "usage_limit",nullable = true)
    private int usageLimit;

    //횟수 제한 - True 면 횟수 제한이 있다는 말임.
    @Column(name = "is_usage_limit")
    private Boolean isUsageLimit;

    //active 한 초대코드인지.
    @Column(name = "is_active",nullable = false)
    private Boolean isActive =true;

}

