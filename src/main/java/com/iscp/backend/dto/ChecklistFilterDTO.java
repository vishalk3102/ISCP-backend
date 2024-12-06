package com.iscp.backend.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

@Data
public class ChecklistFilterDTO {

    @Schema(description = "To get the page no.")
    int page;

    @Schema(description = "To get the size", defaultValue = "10")
    int size;

    @Schema(description = "To get the sort filed", example = "controlChecklist")
    String sortField;

    @Schema(description = "To get the sort order",  defaultValue = "asc")
    Boolean sortOrder;

    @Schema(description = "To get the name of the checklist", example ="[\"cctv\",\"gate\"]")
    private List<String> checklist;

    @Schema(description = "To get the Checklist status", example = "Active")
    private Boolean status;

    @Schema(description = "To get the name of the control",  example ="[\"ISIM 1\",\"ISMI\"]")
    private List<String> control;
}
