package com.iscp.backend.dto;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
@Schema(description = "DTO for Control information")
public class ControlDTO {
    @Schema(
            description = "ID of the Control",
            example = "c97d8605-3198-450e-a10d-acdcde222dbb"
    )
    private String controlId;

    @Schema(description = "Name of the Control", example = "Access Control")
    private String controlName;

    @Schema(description = "Detailed description of the Control")
    private String description;

    @Schema(description = "Status of the Control item", example = "true")
    private Boolean status;

    @Schema(description = "Control  Category DTO to get Control Category Name", example = "ISMS 1")
    private ControlCategoryCreateDTO controlCategory;

}
