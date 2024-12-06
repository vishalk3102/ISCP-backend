package com.iscp.backend.repositories;

import com.iscp.backend.models.Department;
import com.iscp.backend.models.Enum;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.Set;

/**
 * Repository interface for accessing {@link Department} entities.
 */
@Repository
public interface DepartmentRepository extends JpaRepository<Department, String> {

    /**
     * Check if department with the specified name exist.
     *
     * @param departmentType name of the department.
     * @return {@code true} if the department with the given name exists, {@code false} otherwise.
     */
    @Query("SELECT CASE WHEN COUNT(d) > 0 THEN TRUE ELSE FALSE END FROM Department d WHERE d.departmentName = :departmentName")
    boolean existByDepartmentName(@Param("departmentName") Enum.DepartmentType departmentType);


    /**
     * Find all departments specified in the Set.
     *
     * @param departmentNames Set of department names.
     * @return A Set of {@link Department} entities matches the department names in the set.
     */
    @Query("SELECT d FROM Department d WHERE d.departmentName IN :departmentNames")
    Set<Department> findAllByName(@Param("departmentNames") Set<Enum.DepartmentType> departmentNames);


    /**
     * Fetch a Department entity by its name.
     *
     * @param departmentType name of the department.
     * @return A {@link Department} Entity if found.
     */
    Optional<Department> findByDepartmentName(Enum.DepartmentType departmentType);
}
