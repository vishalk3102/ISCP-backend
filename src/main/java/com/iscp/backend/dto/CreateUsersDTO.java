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
@Schema(description = "DTO for User Creation")
public class CreateUsersDTO {

    @Schema(description = "ID of the User", example = "402880e5831d36fd01831d37158e0000")
    private String userId;

    @Schema(description = "Name of the user", example = "Annie")
    private String name;

    @Schema(description = "Indicates if the user account is active", example = "true")
    private Boolean status;

    @Schema(description = "Employee code of users", example = "Emp01")
    private String empCode;

    @Schema(description = "Email id of users", example = "annie@contata.in")
    private String userEmailId;

    @Schema(description = "Set of department IDs associated with the user", example = "[Administration, SysAdmin, Human_Resource]")
    private Set<Enum.DepartmentType> departments;

    @Schema(description = "Set of role IDs associated with the user", example = "[Uploader, Admin, Viewer]")
    private Set<Enum.RoleType> roles;
}

