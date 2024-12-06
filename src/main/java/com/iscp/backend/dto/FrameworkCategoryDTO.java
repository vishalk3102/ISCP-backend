package com.iscp.backend.dto;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

@Data
@Schema(description = "DTO for Framework Category")
public class FrameworkCategoryDTO {

    @Schema(description = "ID of the Framework Category")
    private String frameworkCategoryId;

    @Schema(description = "Name of the Framework Category")
    private String frameworkCategoryName;

    @Schema(description = "List of Frameworks")
    private List<FrameworkDTO> frameworks;

}
