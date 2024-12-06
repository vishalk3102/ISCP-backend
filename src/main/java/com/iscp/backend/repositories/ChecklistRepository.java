package com.iscp.backend.repositories;

import com.iscp.backend.models.Checklist;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.Set;


@Repository
public interface ChecklistRepository extends JpaRepository<Checklist,String> {
    Optional<Checklist> findByControlChecklist(String controlChecklist);

    @Query("SELECT c FROM Checklist c WHERE c.controlChecklist IN :controlChecklists")
    Set<Checklist> findByControlChecklistIn(Set<String> controlChecklists);

    Set<Checklist> findByControlChecklistInAndControl_ControlName(Set<String> controlChecklists, String controlName);

    List<Checklist> findByControl_ControlName(String controlName, Sort controlChecklist);

    Page<Checklist> findAll(Specification<Checklist> spec, Pageable pageable);

    @Query("SELECT c FROM Checklist c WHERE c.control.controlName IN :controls")
    List<Checklist> findByControlList(@Param("controls") List<String> controls);

    Optional<Checklist> findByControlChecklistAndControl_ControlName(String controlChecklist, String controlName);
}
