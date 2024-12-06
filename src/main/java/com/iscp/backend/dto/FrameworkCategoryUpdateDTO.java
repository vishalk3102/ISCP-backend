package com.iscp.backend.dto;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "DTO for creating a new Framework Category")
public class FrameworkCategoryUpdateDTO {

    @Schema(description = "Id of the Framework Category")
    private String frameworkCategoryId;

    @Schema(description = "Name of the Framework Category")
    private String frameworkCategoryName;
}
