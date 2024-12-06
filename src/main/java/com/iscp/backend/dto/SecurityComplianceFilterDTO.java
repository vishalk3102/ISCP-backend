package com.iscp.backend.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Set;

@Data
public class SecurityComplianceFilterDTO {

  @Schema(description = "To get the page no.")
  int page;

  @Schema(description = "To get the size")
  int size;

  @Schema(description = "To get the sort filed",  defaultValue = "creationTime")
  String sortField;

  @Schema(description = "To get the sort order",  defaultValue = "asc")
  String sortOrder;

  @Schema(description = "To get the name of the framework", example ="[\"Information Security Framework 1\",\"Information Security Framework\"]")
  private List<String> framework;

  @Schema(description = "To get the name of the framework category", example = "Framework Category 1002")
  private List<String> frameworkCategory;

  @Schema(description = "To get the name of the control",  example ="[\"ISIM 1\",\"ISMI\"]")
  private List<String> control;

  @Schema(description = "To get the name of the control category",  example ="[\"Access Control\",\"Access Control 1\"]")
  private List<String> controlCategory;

  @Schema(description = "To get the name of the checklist", example ="[\"cctv\",\"gate\"]")
  private List<String> complianceChecklist;

  @Schema(description = "To get the name of the department",  example ="[\"Human_Resource\",\"SysAdmin\"]")
  private List<String> department;

  @Schema(description = "To get the evidence status", example = "Pending")
  private String evidenceStatus;

  @Schema(description = "To get the compliance status", example = "Active")
  private Boolean status;

  @Schema(description = "To get the compliance start date", example = "May 2024")
  private String startDate;

  @Schema(description = "To get the compliance end date", example = "May 2025")
  private String endDate;
}
