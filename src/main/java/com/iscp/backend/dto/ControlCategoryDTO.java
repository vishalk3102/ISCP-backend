package com.iscp.backend.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.util.List;


@Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
@Schema(description = "DTO for Control Category information")
public class ControlCategoryDTO {
    @Schema(
            description = "ID of the Control Category",
            example = "c97d8605-3198-450e-a10d-acdcde222dbb"
    )
    private String controlCategoryId;

    @Schema(description = "Name of the control category")
    private String controlCategoryName;

    @Schema(description = "List of controls associated with this control category")
    private List<ControlDTO> controlList;
}
