package com.iscp.backend.repositories;

import com.iscp.backend.models.Enum;
import com.iscp.backend.models.Permission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository interface for accessing {@link Permission} entities.
 */
@Repository
public interface PermissionRepository extends JpaRepository<Permission, String> {

    /**
     * Fetch a Permission entity by its name.
     *
     * @param rolePermissions name of the permission.
     * @return A {@link Permission} Entity if found.
     */
    Optional<Permission> findByRolePermissions(Enum.PermissionType rolePermissions);
}