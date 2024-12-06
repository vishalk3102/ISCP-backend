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
@Schema(description = "DTO for updating existing Control Category ")
public class ControlCategoryUpdateDTO {
    @Schema(
            description = "ID of the Control Category",
            example = "c97d8605-3198-450e-a10d-acdcde222dbb"
    )
    private String controlCategoryId;

    @Schema(description = "Updated name of the control category", example = "Updated Compliance")
    private String controlCategoryName;
}
