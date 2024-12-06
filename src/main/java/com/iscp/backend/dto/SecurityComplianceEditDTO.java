package com.iscp.backend.dto;

import com.iscp.backend.models.Enum;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.util.List;
import java.util.Set;

@Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "DTO for updating a new security compliance")
public class SecurityComplianceEditDTO {

    @Schema(description = "ID of the Security Compliance", example = "402880e5831d36fd01831d37158e0000")
    private String securityId;

    @Schema(description = "record id of each new created record for each checklist", example = "202409-001")
    private String recordId;

    @Schema(description = "compliance id to map all the checklist")
    private String complianceId;

    @Schema(description = "Name of the Framework", example = "ISO27XXX")
    private String frameworkName;

    @Schema(description = "Name of the Control", example = "Access Control 2.X A")
    private String controlName;

    @Schema(description = "set of the Checklist", example = "[\"CCTV\",\"GATE\"]")
    private Set<String> checklistName;

    @Schema(description = "Periodicity of Compliance", example = "Annually")
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