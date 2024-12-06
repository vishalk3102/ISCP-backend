package com.iscp.backend.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;
import java.util.Set;

@Data
public class SecurityComplianceDTO {

    @Schema(description = "ID of the Security Compliance", example = "402880e5831d36fd01831d37158e0000")
    private String securityId;

    @Schema(description = "record id of each new created record for each checklist", example = "202409-001")
    private String recordId;

    @Schema(description = "compliance id to map all the checklist", example = "302880e5831d36fd01831d37158e0000")
    private String complianceId;

    @Schema(description = "Name of the Framework", example = "ISO27XXX")
    private String frameworkName;

    @Schema(description = "Name of the Framework Category")
    private String frameworkCategory;

    @Schema(description = "Name of the Control", example = "Access Control 2.X A")
    private String controlName;

    @Schema(description = "Name of the control category")
    private String controlCategory;

    @Schema(description = "Name of the Checklist", example = "CCTV")
    private String checklistName;

    @Schema(description = "Periodicity of Compliance", example = "annually")
    private String periodicity;

    @Schema(description = "Department TO Handle", example = "IT")
    private Set<String> departments;

    @Schema(description = "List of All associated list", example = "file.pdf")
    private List<String> evidenceList;

    @Schema(description = "Evidence Compliance Status",example = "True")
    private Boolean evidenceComplianceStatus;

    @Schema(description = "Comments on Evidence", example = "Evidence uploaded")
    private String evidenceComments;
}
