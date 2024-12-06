package com.iscp.backend.repositories;

import com.iscp.backend.models.Framework;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FrameworkRepository extends JpaRepository<Framework, String> {
    Optional<Framework> findByFrameworkName(String frameworkName);

    List<Framework> findByFrameworkCategory_FrameworkCategoryName(String frameworkCategoryName, Sort frameworkName);

    Page<Framework> findAll(Specification<Framework> spec, Pageable pageable);
    Optional<Framework> findByFrameworkId(String frameworkId);
}
