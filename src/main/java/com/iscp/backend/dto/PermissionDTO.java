package com.iscp.backend.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import com.iscp.backend.models.Enum;

@Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "DTO for permission")
public class PermissionDTO {
    @Schema(
            description = "Unique identifier for each permission",
            example = "402880e5831d36fd01831d37158e0000"
    )
    private String permissionId;

    @Schema(description = "Permission name", example = "Uploader")
    private Enum.PermissionType rolePermissions;
}
