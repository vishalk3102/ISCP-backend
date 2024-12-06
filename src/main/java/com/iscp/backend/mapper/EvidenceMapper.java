package com.iscp.backend.mapper;

import com.iscp.backend.dto.EvidenceDTO;
import com.iscp.backend.models.Evidence;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

/**
 * Mapper interface for mapping between Evidence entities and EvidenceDTO.
 */
@Mapper(componentModel = "spring")
public interface EvidenceMapper {

    /**
     * Mapping Evidence Entity to Evidence DTO.
     *
     * @param evidence the Evidence entity to convert.
     * @return the converted EvidenceDTO.
     */
    @Mapping(target = "timestamp", source = "timestamp")
    @Mapping(target = "checklistName", source = "checklist.controlChecklist")
    @Mapping(target = "userId", source = "user.userId")
    @Mapping(target = "securityId", source = "securityCompliance.securityId")
    EvidenceDTO toEvidenceDto(Evidence evidence);


    /**
     * Mapping Evidence DTO to Evidence Entity.
     *
     * @param evidenceDTO the Evidence DTO to convert.
     * @return the converted Evidence Entity.
     */
    Evidence toEvidenceEntity(EvidenceDTO evidenceDTO);
}
