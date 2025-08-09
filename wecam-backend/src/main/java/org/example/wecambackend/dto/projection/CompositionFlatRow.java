package org.example.wecambackend.dto.projection;

public interface CompositionFlatRow {
    Long   getDepartmentId();
    String getDepartmentName();
    Long   getUserId();
    String getUserName();
    String getUserCouncilRole();
    Long   getDepartmentRoleId();
    String getDepartmentRoleName();
    String getExitType();
    String getExpulsionReason();
}
