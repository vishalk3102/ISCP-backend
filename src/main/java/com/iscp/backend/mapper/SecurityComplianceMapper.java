package com.iscp.backend.mapper;

import com.iscp.backend.dto.*;
import com.iscp.backend.models.*;
import com.iscp.backend.models.Enum;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Mapper interface for mapping between SecurityCompliance entities and SecurityCompliance DTO.
 */
@Mapper(componentModel = "spring")
public interface SecurityComplianceMapper {

    /**
     * Mapping SecurityComplianceCreateDTO to SecurityCompliance Entity.
     *
     * @param securityComplianceCreateDTO the securityComplianceCreateDTO to convert.
     * @return the converted SecurityCompliance Entity.
     */
    @Mapping(target = "securityId", ignore = true)
    @Mapping(target = "framework", ignore = true)
    @Mapping(target = "control", ignore = true)
    @Mapping(target = "checklist", ignore = true)
    @Mapping(target = "departments", ignore = true)
    @Mapping(target = "creationTime", ignore = true)
    SecurityCompliance toSecurityComplianceEntity(SecurityComplianceCreateDTO securityComplianceCreateDTO);


    /**
     * Mapping List of SecurityCompliance Entity to List of SecurityComplianceDTO.
     *
     * @param securityComplianceList the list of SecurityCompliance to convert.
     * @return the converted list of SecurityComplianceDTO.
     */
    List<SecurityComplianceDTO> toSecurityComplianceDTO (List<SecurityCompliance> securityComplianceList);


    /**
     * Mapping SecurityCompliance Entity to SecurityComplianceDTO.
     *
     * @param securityCompliance the SecurityCompliance Entity to convert.
     * @return the converted SecurityComplianceDTO.
     */
    @Mapping(target = "frameworkName", source = "framework", qualifiedByName = "mapFrameworkEntityToName")
    @Mapping(target = "controlName", source = "control", qualifiedByName = "mapControlEntityToName")
    @Mapping(target = "checklistName", source = "checklist", qualifiedByName = "mapChecklistEntityToName")
    @Mapping(target = "departments", source = "departments", qualifiedByName = "departmentToDepartment")
    @Mapping(target = "periodicity", source = "periodicity", qualifiedByName = "mapPeriodicityEnumToName")
    @Mapping(target = "frameworkCategory", source = "framework", qualifiedByName = "mapFrameworkEntityToCategory")
    @Mapping(target = "controlCategory", source = "control",qualifiedByName = "mapControlEntityToCategory")
    @Mapping(target = "evidenceList", ignore = true)
    SecurityComplianceDTO toSecurityComplianceDTO(SecurityCompliance securityCompliance);


    /**
     * Mapping SecurityCompliance Entity and evidenceList to SecurityComplianceDTO.
     *
     * @param securityCompliance the SecurityCompliance to convert.
     * @param evidenceList the EvidenceList to map.
     * @return the converted SecurityComplianceDTO.
     */
    @Mapping(target = "frameworkName", source = "securityCompliance.framework", qualifiedByName = "mapFrameworkEntityToName")
    @Mapping(target = "controlName", source = "securityCompliance.control", qualifiedByName = "mapControlEntityToName")
    @Mapping(target = "checklistName", source = "securityCompliance.checklist", qualifiedByName = "mapChecklistEntityToName")
    @Mapping(target = "departments", source = "securityCompliance.departments", qualifiedByName = "departmentToDepartment")
    @Mapping(target = "periodicity", source = "securityCompliance.periodicity", qualifiedByName = "mapPeriodicityEnumToName")
    @Mapping(target = "frameworkCategory", source = "securityCompliance.framework", qualifiedByName = "mapFrameworkEntityToCategory")
    @Mapping(target = "controlCategory", source = "securityCompliance.control", qualifiedByName = "mapControlEntityToCategory")
    @Mapping(target = "evidenceList", source = "evidenceList")
    SecurityComplianceDTO toSecurityComplianceDTO(SecurityCompliance securityCompliance, List<String> evidenceList);

    //Custom mapping for Framework to String (frameworkName)
    @Named("mapFrameworkEntityToName")
    default String mapFrameworkEntityToName(Framework framework) {
        return framework != null ? framework.getFrameworkName() : null;
    }

    //Custom mapping for FrameworkCategory to String (frameworkCategoryName)
    @Named("mapFrameworkEntityToCategory")
    default String mapFrameworkEntityToCategory(Framework framework) {
        return framework != null ? framework.getFrameworkCategory().getFrameworkCategoryName() : null;
    }

    //Custom mapping for Control to String (controlName)
    @Named("mapControlEntityToName")
    default String mapControlEntityToName(Control control) {
        return control != null ? control.getControlName() : null;
    }

    //Custom mapping for ControlCategory to String (controlCategoryName)
    @Named("mapControlEntityToCategory")
    default String mapControlEntityToCategory(Control control) {
        return control != null ? control.getControlCategory().getControlCategoryName() : null;
    }

    //Custom mapping for Checklist to String (checklistName)
    @Named("mapChecklistEntityToName")
    default String mapChecklistEntityToName(Checklist checklist) {
        return checklist != null ? checklist.getControlChecklist() : null;
    }

    //Custom mapping for Periodicity Enum to String (periodicity)
    @Named("mapPeriodicityEnumToName")
    default String mapPeriodicityEnumToName(Enum.Periodicity periodicity) {
        return periodicity != null ? periodicity.name(): null;
    }

    //Custom mapping for Department Enum to String (departmentNames)
    @Named("departmentToDepartment")
    static Set<String> mapDepartmentsToNames(Set<Department> departments) {
        return departments.stream()
                .map(department -> department.getDepartmentName().toString())
                .collect(Collectors.toSet());
    }


    /**
     * Mapping SecurityComplianceEditDTO to SecurityCompliance Entity
     *
     * @param securityComplianceEditDTO the SecurityComplianceEditDTO to convert.
     * @return the converted SecurityCompliance Entity.
     */
    @Mapping(target = "securityId", ignore = true)
    @Mapping(target = "framework", ignore = true)
    @Mapping(target = "control", ignore = true)
    @Mapping(target = "checklist", ignore = true)
    @Mapping(target = "departments", ignore = true)
    @Mapping(target = "creationTime", ignore = true)
    SecurityCompliance toSecurityComplianceEditEntity(SecurityComplianceEditDTO securityComplianceEditDTO);


    /**
     * Mapping SecurityComplianceCreateDTO to SecurityComplianceEditDTO.
     *
     * @param securityComplianceCreateDTO the SecurityComplianceCreateDTO to convert.
     * @return the converted SecurityComplianceEditDTO.
     */
    @Mapping(source="eventDate", target="eventDate")
    SecurityComplianceEditDTO toSecurityComplianceEditDTO(SecurityComplianceCreateDTO securityComplianceCreateDTO);


    /**
     * Mapping SecurityComplianceCreateDTO to SecurityComplianceCheckDTO.
     *
     * @param securityComplianceCreateDTO the SecurityComplianceCreateDTO to convert.
     * @return the converted SecurityComplianceCheckDTO.
     */
    @Mapping(target = "framework", source = "frameworkName")
    @Mapping(target = "control", source = "controlName")
    @Mapping(target = "checklist", source = "checklistName")
    @Mapping(target = "periodicity", source = "periodicity")
    @Mapping(target = "status", source = "evidenceComplianceStatus")
    SecurityComplianceCheckDTO toCheckDTO(SecurityComplianceCreateDTO securityComplianceCreateDTO);
}