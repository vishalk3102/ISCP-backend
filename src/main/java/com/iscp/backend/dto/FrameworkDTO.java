package com.iscp.backend.dto;
import java.sql.Date;
import java.time.LocalDate;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "DTO for Framework information")
public class FrameworkDTO {

        @Schema(description = "ID of the Framework", example = "402880e5831d36fd01831d37158e0000")
        private String frameworkId;

        @Schema(description = "Name of the Framework", example = "Information Security Framework")
        private String frameworkName;

        @Schema(description = "Start date of the Framework", example = "2024-01-01")
        private String startDate;

        @Schema(description = "End date of the Framework", example = "2024-12-31")
        private String endDate;

        @Schema(description = "Detailed description of the Framework", example = "This framework covers information security and compliance.")
        private String description;

        @Schema(description = "Status of the Framework", example = "true")
        private Boolean status;

        @Schema(description = "Framework Category DTO to get Framework Name", example = "ISMS 1")
        private FrameworkCategoryCreateDTO frameworkCategory;
}