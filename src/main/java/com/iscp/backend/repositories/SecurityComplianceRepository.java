package com.iscp.backend.repositories;

import com.iscp.backend.models.Enum;
import com.iscp.backend.models.SecurityCompliance;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;

import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SecurityComplianceRepository extends JpaRepository<SecurityCompliance,String> {

    Optional<SecurityCompliance> findBySecurityId(String securityId);

    List<SecurityCompliance> findAllByComplianceId(String complianceId);

    Page<SecurityCompliance> findAll(Specification<SecurityCompliance> spec, Pageable pageable);

    List<SecurityCompliance> findByFramework_FrameworkNameAndControl_ControlNameAndChecklist_ControlChecklist(
            String frameworkName, String controlName, String checklistName);


    List<SecurityCompliance> findByFramework_FrameworkNameAndControl_ControlNameAndChecklist_ControlChecklistAndPeriodicityAndDepartments_departmentNameAndEvidenceComplianceStatus(
            String frameworkName, String controlName, String checklistName, Enum.Periodicity periodicity, Enum.DepartmentType department, Boolean evidenceComplianceStatus);



    List<SecurityCompliance> findByFramework_FrameworkNameAndControl_ControlNameAndChecklist_ControlChecklistAndPeriodicityAndEvidenceComplianceStatus(
            String frameworkName, String controlName, String checklistName, Enum.Periodicity periodicity, Boolean evidenceComplianceStatus);


    List<SecurityCompliance> findByFramework_FrameworkNameAndControl_ControlNameAndPeriodicityAndEvidenceComplianceStatus(String framework, String control, Enum.Periodicity periodicity, Boolean evidenceComplianceStatus);


//    List<SecurityCompliance> findByFramework_FrameworkNameAndControl_ControlNameAndChecklist_ControlChecklistAndPeriodicityAndEvidenceComplianceStatus(String frameworkName, String controlName, String checklistName, Enum.Periodicity periodicity, Boolean evidenceComplianceStatus);

//    List<SecurityCompliance> findByFramework_FrameworkNameAndControl_ControlNameAndPeriodicityAndEvidenceComplianceStatus(String framework, String control, Enum.Periodicity periodicity, Boolean evidenceComplianceStatus);
}
