package com.iscp.backend.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

@Data
public class FrameworkFilterDTO {

    @Schema(description = "To get the page no.")
    int page;

    @Schema(description = "To get the size", defaultValue = "10")
    int size;

    @Schema(description = "To get the sort filed",  example = "framework")
    String sortField;

    @Schema(description = "To get the sort order",  example = "true")
    Boolean sortOrder;

    @Schema(description = "To get the name of the framework category", example ="[\"Information Security Framework\"]")
    private List<String> frameworkCategory;

    @Schema(description = "To get the name of the framework", example ="[\"Information Security Framework 1\",\"Information Security Framework\"]")
    private List<String> framework;


    @Schema(description = "To get the framework status", example = "Active")
    private Boolean status;

    @Schema(description = "To get the framework start date", example = "May 2024")
    private String startDate;

    @Schema(description = "To get the framework end date", example = "May 2025")
    private String endDate;

}
