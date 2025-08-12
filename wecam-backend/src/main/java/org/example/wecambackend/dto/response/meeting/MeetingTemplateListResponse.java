package org.example.wecambackend.dto.response.meeting;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MeetingTemplateListResponse {
    
    // 템플릿 id
    private Long templateId;
    
    // 템플릿 이름
    private String templateName;

    // 기본값 여부
    private Boolean isDefault;
}
