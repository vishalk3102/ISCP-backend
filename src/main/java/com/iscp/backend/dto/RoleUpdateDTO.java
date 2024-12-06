package com.iscp.backend.dto;

import com.iscp.backend.models.Enum;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.util.Set;


@Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RoleUpdateDTO {
    @Schema(
            description = "Unique identifier for each role entry",
            example = "402880e5831d36fd01831d37158e0000"
    )
    private String roleId;

    @Schema(description = "Name of the role", example = "Admin")
    private Enum.RoleType roleName;

    @Schema(description = "Set of permission types associated with the role", example = "[ADMIN, VIEWER]")
    private Set<Enum.PermissionType> permissions;

    @Schema(description = "Status of the Role item", example = "true")
    private Boolean status;
}
