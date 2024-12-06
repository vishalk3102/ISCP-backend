package com.iscp.backend.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
@Schema(description = "DTO for Checklist information")
public class ChecklistRequest {
    @Schema(description = "Name of the Checklist", example = "Security Checklist")
    private String controlChecklist;

    private String controlName;
}

