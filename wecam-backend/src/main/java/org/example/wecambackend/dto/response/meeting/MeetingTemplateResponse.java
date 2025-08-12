package org.example.wecambackend.dto.response.meeting;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MeetingTemplateResponse {
    
    // 템플릿 id
    private Long templateId;
    
    // 템플릿 이름
    private String templateName;
    
    // 템플릿 설명
    private String description;
    
    // 마크다운 템플릿 내용
    private String content;
}
