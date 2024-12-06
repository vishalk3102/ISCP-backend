package com.iscp.backend.repositories;

import com.iscp.backend.models.Department;
import com.iscp.backend.models.Role;
import com.iscp.backend.models.Users;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

/**
 * Repository interface for accessing {@link Users} entities.
 */
@Repository
public interface UsersRepository extends JpaRepository<Users, String> {

    /**
     * Retrieves an {@link Users} entity based on its EmailId.
     *
     * @param userEmailId the EmailId of user.
     * @return the {@link Users} entity associated with the given email ID, or null if no user is found.
     */
    Users findByUserEmailId(String userEmailId);


    /**
     * Checks whether a {@link Users} entity exists with the given email ID.
     *
     * @param userEmailId the Email ID of the user to check.
     * @return {@code true} if a user exists with the given email ID, {@code false} otherwise.
     */
    @Query("SELECT CASE WHEN COUNT(u) > 0 THEN TRUE ELSE FALSE END FROM Users u WHERE u.userEmailId = :userEmailId")
    Boolean existByUserEmailId(String userEmailId);


    /**
     * Retrieves an {@link Users} entity based on its EmpCode.
     *
     * @param empCode the EmpCode of user.
     * @return the {@link Users} entity associated with the given EmpCode, or null if no user is found.
     */
    Optional<Users> findByempCode(String empCode);


    /**
     * Retrieves a paginated list of {@link Users} entities based on the provided {@link Specification} and {@link Pageable}.
     *
     * @param spec the {@link Specification} to filter the list of users.
     * @param pageable the {@link Pageable} object containing pagination information.
     * @return a {@link Page} of {@link Users} entities that match criteria.
     */
    Page<Users> findAll(Specification<Users> spec, Pageable pageable);


    /**
     * Retrieves all department names associated with a given user EmailId.
     *
     * @param userEmailId the Email ID of the user.
     * @return a list of department associated with the given user EmailId.
     */
    @Query("SELECT d.departmentName FROM Users u JOIN u.departments d WHERE u.userEmailId = :userEmailId")
    List<String> findAllDepartmentByUserEmailId(String userEmailId);


    /**
     * Retrieves all {@link Department} entities associated with the logged-in user.
     *
     * @param userEmailId the Email ID of the user.
     * @return a list of {@link Department} entities associated with the given logged-in user EmailId.
     */
    @Query("SELECT d FROM Department d JOIN d.users u WHERE u.userEmailId = :userEmailId")
    List<Department> findLogInUserDepartment(@Param("userEmailId") String userEmailId);


    /**
     * Retrieves all {@link Role} entities associated with the logged-in user.
     *
     * @param userEmailId the Email ID of the user.
     * @return a list of {@link Role} entities associated with the given logged-in user EmailId.
     */
    @Query("SELECT r FROM Role r JOIN r.users u WHERE u.userEmailId = :userEmailId")
    List<Role> findAllRolesByUserEmailId(@Param("userEmailId") String userEmailId);
}

