package org.example.wecambackend.dto.request.affiliation;

import lombok.Getter;
import org.example.model.enums.AuthenticationType;

// AffiliationApprovalRequest.java
@Getter
public class AffiliationApprovalRequest {
    private Long userId;
    private AuthenticationType authType;
}
