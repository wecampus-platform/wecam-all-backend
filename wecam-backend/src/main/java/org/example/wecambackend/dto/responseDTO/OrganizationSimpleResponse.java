package org.example.wecambackend.dto.responseDTO;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.example.model.Organization;

@Getter
@AllArgsConstructor
public class OrganizationSimpleResponse {
    private Long id;
    private String name;
    private int level;

    public static OrganizationSimpleResponse fromEntity (Organization org) {
        return new OrganizationSimpleResponse(org.getOrganizationId(), org.getOrganizationName(), org.getLevel());
    }
}
