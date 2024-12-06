package com.iscp.backend.dto;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.iscp.backend.components.DepartmentTypeSerializer;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import com.iscp.backend.models.Enum;

@Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "DTO for Department information")
public class DepartmentDTO {
    @Schema(
            description = "Unique identifier for each department",
            example = "402880e5831d36fd01831d37158e0000"
    )
    private String departmentId;

    @JsonSerialize(using = DepartmentTypeSerializer.class)
    @Schema(description = "Name of the department", example = "Administration")
    private Enum.DepartmentType departmentName;
}
