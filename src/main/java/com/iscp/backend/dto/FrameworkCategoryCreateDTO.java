package com.iscp.backend.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "DTO for creating a new Framework Category")
public class FrameworkCategoryCreateDTO {

    @Schema(description = "Name of the Framework Category", example = "ISMS 1")
    private String frameworkCategoryName;
}
