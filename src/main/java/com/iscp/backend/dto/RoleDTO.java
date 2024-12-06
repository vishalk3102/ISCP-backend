package com.iscp.backend.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import com.iscp.backend.models.Enum;

import java.util.Set;

@Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "DTO for Role information")
public class RoleDTO {
    @Schema(
            description = "Unique identifier for each role entry",
            example = "402880e5831d36fd01831d37158e0000"
    )
    private String roleId;

    @Schema(description = "Name of the role", example = "Admin")
    private Enum.RoleType roleName;

    @Schema(description = "Status of the Role item", example = "true")
    private Boolean status;

    @Schema(description = "Set of permission IDs associated with the role", example = "[\"Viewer\",\"Admin\"]")
    private Set<String> permissions;
}
