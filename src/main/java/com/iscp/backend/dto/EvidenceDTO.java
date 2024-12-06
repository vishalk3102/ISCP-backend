package com.iscp.backend.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.sql.Timestamp;

@Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "DTO for Evidence Upload")
public class EvidenceDTO {
    @Schema(description = "Name of File", example = "AccessCard.png")
    private String fileName;

    @Schema(description = "Type of file", example = "video/mp4")
    private String fileType;

    @Schema(description = "Time of uploading image", example = "2024-09-26 11:25:12.639000")
    private Timestamp timestamp;

    @Schema(description = "path of file", example = ".\\uploads\\AccessCard.png")
    private String fileReference;

    @Schema(description = "checklist Name", example = "CCTV")
    private String checklistName;

    @Schema(description = "User ID", example = "1213956c-3a21-43b0-a357-364828f48e58")
    private String userId;

    @Schema(description = "Security Compliance ID", example = "security_456")
    private String securityId;
}
