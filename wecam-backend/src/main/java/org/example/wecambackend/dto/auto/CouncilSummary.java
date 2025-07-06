package org.example.wecambackend.dto.auto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.example.model.enums.MemberRole;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CouncilSummary {
    private Long id;
    private String name;
    private MemberRole memberRole;
}
