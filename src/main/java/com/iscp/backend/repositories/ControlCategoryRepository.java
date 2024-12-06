package com.iscp.backend.repositories;

import com.iscp.backend.models.Control;
import com.iscp.backend.models.ControlCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;


@Repository
public interface ControlCategoryRepository extends JpaRepository<ControlCategory, String> {

  Optional<ControlCategory> findByControlCategoryName(String controlCategoryName);

  Optional<ControlCategory> findByControlCategoryId(String controlId);
}
