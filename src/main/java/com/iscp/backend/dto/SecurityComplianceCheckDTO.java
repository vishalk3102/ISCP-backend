package com.iscp.backend.dto;

import com.iscp.backend.models.Enum;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;
import java.util.Set;

@Data
public class SecurityComplianceCheckDTO {
    @Schema(description = "name of the framework")
    String framework;

    @Schema(description = "name of the control")
    String control;

    @Schema(description = "list of the check list")
    Set<String> checklist;

    @Schema(description = "name of the periodicity")
    Enum.Periodicity periodicity;

    @Schema(description = "Compliance status")
    private Boolean status;

}
