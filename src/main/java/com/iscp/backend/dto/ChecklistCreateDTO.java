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
@Schema(description = "DTO for creating new checklist")
public class ChecklistCreateDTO {
    @Schema(
            description = "ID of the Checklist",
            example = "c97d8605-3198-450e-a10d-acdcde222dbb"
    )
    private String checklistId;

    @Schema(description = "Control checklist item", example = "Verify user authentication")
    private String controlChecklist;

    @Schema(description = "Detailed description of the Checklist")
    private String description;

    @Schema(description = "Status of the checklist item", example = "true")
    private Boolean status;

    @Schema(description = "Name of the Control", example = "control access")
    private String controlName;
}
