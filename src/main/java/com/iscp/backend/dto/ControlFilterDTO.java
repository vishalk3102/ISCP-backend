package com.iscp.backend.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

@Data
public class ControlFilterDTO {

    @Schema(description = "To get the page no.")
    int page;

    @Schema(description = "To get the size", defaultValue = "10")
    int size;

    @Schema(description = "To get the sort filed",  example = "controlName")
    String sortField;

    @Schema(description = "To get the sort order",  defaultValue = "asc")
    Boolean sortOrder;

    @Schema(description = "To get the name of the framework category", example ="[\"ISMS\"]")
    private List<String> controlCategory;

    @Schema(description = "To get the name of the framework", example ="[\"ACCESS CONTROL\"]")
    private List<String> control;

    @Schema(description = "To get the framework status", example = "Active")
    private Boolean status;
}