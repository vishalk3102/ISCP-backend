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
@Schema
public class MultiEntityUploadResponse {
    private List<ControlCategoryDTO> controlCategoryResults;
    private List<ControlDTO> controlResults;
    private List<ChecklistDTO> checklistResults;
}
