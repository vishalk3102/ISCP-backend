package com.iscp.backend.dto;


import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Data
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
@Schema(description = "DTO for creating new Control Category")
public class ControlCategoryCreateDTO {

    @Schema(
            description = "ID of the Control Category",
            example = "c97d8605-3198-450e-a10d-acdcde222dbb"
    )
    private String controlCategoryId;

    @NotBlank(message = "Control category name is required")
    @Schema(description = "Name of the control category", example = "Compliance")
    private String controlCategoryName;
}
