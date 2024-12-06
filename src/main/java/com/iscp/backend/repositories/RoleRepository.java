package com.iscp.backend.repositories;

import com.iscp.backend.models.Enum;
import com.iscp.backend.models.Role;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.Set;

/**
 * Repository interface for accessing {@link Role} entities.
 */
@Repository
public interface RoleRepository extends JpaRepository<Role, String> {

    /**
     * Find all roles specified in the Set.
     *
     * @param roleNames Set of role names.
     * @return A Set of {@link Role} entities matches the role names in the set.
     */
    @Query("SELECT r FROM Role r WHERE r.roleName IN :roleNames")
    Set<Role> findAllByName(@Param("roleNames") Set<Enum.RoleType> roleNames);


    /**
     * Fetch a Role entity by its name.
     *
     * @param roleName name of the role.
     * @return A {@link Role} Entity if found.
     */
    Optional<Role> findByRoleName(Enum.RoleType roleName);


    /**
     * Retrieves a paginated list of {@link Role} entities based on the provided {@link Specification} and {@link Pageable}.
     *
     * @param spec the {@link Specification} to filter the list of roles.
     * @param pageable the {@link Pageable} object containing pagination information.
     * @return a {@link Page} of {@link Role} entities that match criteria.
     */
    Page<Role> findAll(Specification<Role> spec, Pageable pageable);
}