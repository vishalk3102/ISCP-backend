package com.iscp.backend.repositories;

import com.iscp.backend.models.Control;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;


@Repository
public interface ControlRepository extends JpaRepository<Control,String> {
    Optional<Control> findByControlName(String controlName);
    List<Control> findByControlCategory_ControlCategoryName(String controlCategoryName, Sort controlName);
    Page<Control> findAll(Specification<Control> spec, Pageable pageable);
    Optional<Control> findByControlId(String controlId);
}
