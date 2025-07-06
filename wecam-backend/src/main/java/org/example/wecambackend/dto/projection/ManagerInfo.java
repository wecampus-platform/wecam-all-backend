package org.example.wecambackend.dto.projection;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ManagerInfo {
    private Long userId;
    private String userName;
}
