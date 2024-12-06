package com.iscp.backend.dto;

import com.iscp.backend.models.Enum;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class SecurityComplianceExistsDTO {

    @Schema(description = "Give the info whether compliance exists or not")
    boolean isExisted;

    @Schema(description = "name of the existed compliance")
    String checklist;

    @Schema(description = "name of the periodicity")
    Enum.Periodicity periodicity;

    @Schema(description = "name of the department")
    Enum.DepartmentType department;

}