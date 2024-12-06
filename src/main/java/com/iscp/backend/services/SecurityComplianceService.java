package com.iscp.backend.services;

import com.iscp.backend.dto.*;
import com.iscp.backend.exceptions.*;
import org.springframework.core.io.ByteArrayResource;

import java.io.IOException;
import java.util.List;

/**
 * Service Interface for managing Security Compliance.
 */
public interface SecurityComplianceService {

    /**
     * Add or update a list of security compliance based on the provided list of security compliance DTO.
     *
     * @param securityComplianceCreateDTOList a list of SecurityComplianceCreateDTO containing details of security compliance to be added or updated.
     * @return a list of {@link SecurityComplianceDTO} containing added or updated security compliance.
     * @throws SecurityComplianceNotFoundException if a security ID is not found in the repository.
     * @throws ChecklistNotFoundException if any of the specified checklist does not exist.
     * @throws FrameworkNotFoundException if the specified framework does not exist.
     * @throws DepartmentNotFoundException if any of the specified department does not exist.
     * @throws ControlNotFoundException if the specified control does not exist.
     * @throws PeriodicityUpdateDeniedException if the periodicity update is denied
     */
    List<SecurityComplianceDTO> addEditSecurityCompliance(List<SecurityComplianceCreateDTO> securityComplianceCreateDTOList) throws SecurityComplianceNotFoundException, FrameworkNotFoundException, ControlNotFoundException, ChecklistNotFoundException, DepartmentNotFoundException, PeriodicityUpdateDeniedException;


    /**
     * Add a list of security compliance based on the provided list of security compliance DTO.
     *
     * @param securityComplianceCreateDTOList a list of SecurityComplianceCreateDTO containing security compliance details to be added.
     * @return a list of {@link SecurityComplianceDTO} containing added security compliance.
     */
    List<SecurityComplianceDTO> addSecurityCompliance(List<SecurityComplianceCreateDTO> securityComplianceCreateDTOList) throws FrameworkNotFoundException, ControlNotFoundException, ChecklistNotFoundException, DepartmentNotFoundException;


    /**
     * Update a list of security compliance based on the provided list of security compliance DTO.
     *
     * @param securityComplianceEditDTOList a list of SecurityComplianceEditDTO containing security compliance details to be updated.
     * @return a list of {@link SecurityComplianceDTO} containing updated users.
     * @throws SecurityComplianceNotFoundException if a security ID is not found in the repository.
     * @throws FrameworkNotFoundException if the specified framework does not exist.
     * @throws PeriodicityUpdateDeniedException if the periodicity update is denied
     */
    List<SecurityComplianceDTO> editSecurityCompliance(List<SecurityComplianceEditDTO> securityComplianceEditDTOList) throws SecurityComplianceNotFoundException, ChecklistNotFoundException, DepartmentNotFoundException, FrameworkNotFoundException, PeriodicityUpdateDeniedException;


    /**
     * Retrieves a paginated list of filtered security compliance records.
     *
     * @param filter SecurityComplianceFilterDTO containing the filtering criteria to be encapsulated.
     * @return a PaginatedResponse containing SecurityComplianceDTO objects.
     */
    PaginatedResponse<SecurityComplianceDTO> getFilterSecurityCompliancePaginated(SecurityComplianceFilterDTO filter);


    /**
     * Exports security compliance to an Excel.
     *
     * @param filter SecurityComplianceFilterDTO containing the filtering criteria to be encapsulated.
     * @return a ByteArrayResource containing the Excel file data.
     * @throws IOException if an error occurs during export.
     */
    ByteArrayResource exportExcelSecurityCompliance(SecurityComplianceFilterDTO filter) throws IOException;


    /**
     * Retrieves a list of all security compliance from the database.
     *
     * @return a list of {@link SecurityComplianceDTO} containing all security compliance along with their evidences.
     */
    List<SecurityComplianceDTO> getAllSecurityComplianceDTOs();


    /**
     * Retrieves a list of SecurityComplianceDTO for the given list of complianceId.
     *
     * @param complianceIds a list of complianceId whose associated security compliance need to retrieve.
     * @return a list of lists containing {@link SecurityComplianceDTO} corresponding to each complianceId.
     * @throws SecurityComplianceNotFoundException if a security ID is not found in the repository.
     */
    List<List<SecurityComplianceDTO>> getSecurityCompliance(List<String> complianceIds) throws SecurityComplianceNotFoundException;


    /**
     * To check whether a security compliance already exists in the database.
     *
     * @param securityComplianceCheckDTO SecurityComplianceCheckDTO containing records to check against existing records.
     * @return {@code true} if the security compliance already exists, {@code false} otherwise.
     */
    Boolean isSecurityComplianceAlreadyExists(SecurityComplianceCheckDTO securityComplianceCheckDTO);
}
