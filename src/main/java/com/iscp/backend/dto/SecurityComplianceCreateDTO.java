package com.iscp.backend.dto;

import com.iscp.backend.models.Enum;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;
import java.util.Set;

@Data
@Schema(description = "DTO for creating a new security compliance")
public class SecurityComplianceCreateDTO {

    @Schema(description = "ID of the Security Compliance", example = "402880e5831d36fd01831d37158e0000")
    private String securityId;

    @Schema(description = "Record Id of the checklist to get info about it's creation time, for which amd which no it is", example = "24-09/24-05/M1")
    private String recordId;

    @Schema(description = "Compliance Id is used for the for the mapping of all the check list", example = "402880e5831d36fd01831d37158e0000")
    private String complianceId;

    @Schema(description = "Name of the Framework", example = "ISO27XXX")
    private String frameworkName;

    @Schema(description = "Name of the Control", example = "Access Control 2.X A")
    private String controlName;

    @Schema(description = "set of the Checklist", example = "[\"CCTV\",\"GATE\"]")
    private Set<String> checklistName;

    @Schema(description = "Periodicity of Compliance", example = "annually")
    private Enum.Periodicity periodicity;

    @Schema(description = "optional, only used when periodicity is onEvent", example = "May 2024")
    private List<String> eventDate;

    @Schema(description = "Set of Departments", example = "[\"HR\",\"IT\"]")
    private Set<Enum.DepartmentType> departments;

    @Schema(description = "Evidence Compliance Status",example = "True")
    private Boolean evidenceComplianceStatus;

    @Schema(description = "Comments on Evidence", example = "Evidence uploaded")
    private String evidenceComments;

}