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
@Schema(description = "DTO for updating existing control")
public class ControlUpdateDTO {
    @Schema(
            description = "ID of the Control to update",
            example = "c97d8605-3198-450e-a10d-acdcde222dbb",
            required = true
    )
    private String controlId;

    @Schema(description = "Name of the Control", example = "Access Control")
    private String controlName;

    @Schema(description = "Detailed description of the Control")
    private String description;

    @Schema(description = "Status of the Control item", example = "true")
    private Boolean status;

    @Schema(description = "Name of the associated Control Category", example = "ISMS 1")
    private String controlCategoryName;
}
