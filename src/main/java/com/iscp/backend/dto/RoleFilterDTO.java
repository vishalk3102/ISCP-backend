package com.iscp.backend.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;


@Data
public class RoleFilterDTO {
    @Schema(description = "To get the page no.")
    int page;

    @Schema(description = "To get the size", defaultValue = "10")
    int size;

    @Schema(description = "To get the sort filed",  example = "controlName")
    String sortField;

    @Schema(description = "To get the sort order",  defaultValue = "asc")
    Boolean sortOrder;

    @Schema(description = "Name of the role", example = "Admin")
    private String roleName;

    @Schema(description = "Status of the Role item", example = "true")
    private Boolean status;
}
