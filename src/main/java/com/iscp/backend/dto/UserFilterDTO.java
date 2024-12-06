package com.iscp.backend.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

@Data
public class UserFilterDTO {

    @Schema(description = "To get the page no.")
    int page;

    @Schema(description = "To get the size", defaultValue = "10")
    int size;

    @Schema(description = "To get the sort filed",  example = "name")
    String sortField;

    @Schema(description = "To get the sort order",  example = "true")
    Boolean sortOrder;

    @Schema(description = "To get the name of the user", example ="[Annie]")
    private List<String> name;

    @Schema(description = "To get the departments of the user", example ="[Administration, Human_Resource]")
    private List<String> departments;

    @Schema(description = "To get the roles of the user", example ="[Admin]")
    private List<String> roles;

    @Schema(description = "To get the user status", example = "Active")
    private Boolean status;
}
