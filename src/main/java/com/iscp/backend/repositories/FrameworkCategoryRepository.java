package com.iscp.backend.repositories;

import com.iscp.backend.models.FrameworkCategory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface FrameworkCategoryRepository extends JpaRepository<FrameworkCategory,String> {
    Optional<FrameworkCategory> findByFrameworkCategoryName(String frameworkCategory);
}
