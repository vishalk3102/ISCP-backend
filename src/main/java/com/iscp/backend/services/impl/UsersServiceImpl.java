package com.iscp.backend.services.impl;

import com.iscp.backend.components.ExportExcel;
import com.iscp.backend.components.Pagination;
import com.iscp.backend.dto.*;
import com.iscp.backend.exceptions.*;
import com.iscp.backend.models.*;
import com.iscp.backend.mapper.UserMapper;
import com.iscp.backend.repositories.DepartmentRepository;
import com.iscp.backend.repositories.RoleRepository;
import com.iscp.backend.repositories.UsersRepository;
import com.iscp.backend.services.UsersService;
import jakarta.persistence.criteria.Predicate;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Implementation of the {@link UsersService} interface for managing users.
 */
@Service
@AllArgsConstructor
@Slf4j
public class UsersServiceImpl implements UsersService {

    private final UsersRepository userRepository;

    private final DepartmentRepository departmentRepository;

    private final RoleRepository roleRepository;

    private final UserMapper userMapper;

    private final BCryptPasswordEncoder passwordEncoder;

    private final ExportExcel exportExcel;

    /**
     * Retrieves a list of all users from the database.
     *
     * @return a list of {@link UsersDTO} containing all users, or an empty list if no user are found.
     */
    @Override
    public List<UsersDTO> getAllUsers() {
        //Retrieve List of all Departments
        List<Users> users=userRepository.findAll(Sort.by(Sort.Direction.ASC,"name"));

        // Check if the retrieved users list is empty
        if (users.isEmpty()) {
            log.info("No Users found");
            return List.of();
        }
        log.info("Fetched users :{}", users);
        //Convert User Entity to User DTO
        return userMapper.DTO_LIST(users);
    }


    /**
     * Add or update a list of security compliance based on the provided list of security compliance DTO.
     *
     * @param createUsersDTOList a list of CreateUsersDTO containing details of users to be added or updated.
     * @return a list of {@link UsersDTO} containing added or updated users.
     * @throws UserNotFoundException if a user ID is not found in the repository.
     * @throws DepartmentNotFoundException if any of the specified department does not exist.
     * @throws RoleNotFoundException if any of the specified role does not exist.
     * @throws UserEmailAlreadyExistsException if the specified EmailId already exists.
     * @throws UserEmpCodeAlreadyExistsException if the specified EmpCode already exists.
     */
    public List<UsersDTO> addUpdateUsers(List<CreateUsersDTO> createUsersDTOList) throws UserNotFoundException, DepartmentNotFoundException, RoleNotFoundException, UserEmailAlreadyExistsException, UserEmpCodeAlreadyExistsException {
        List<UsersDTO> allUsersList=new ArrayList<>();

        //List to store users which needs to be updated
        List<UpdateUserDTO> updateUserList=new ArrayList<>();

        for(CreateUsersDTO usersDTO : createUsersDTOList){
            String id = usersDTO.getUserId();

            //If userId is blank, then call addUser method to add the user
            if(id.isBlank()) {
                allUsersList.add(addUser(usersDTO));
            }
            //If userId is not found, then throw an exception
            else if(userRepository.findById(usersDTO.getUserId()).isEmpty()) {
                throw new UserNotFoundException();
            }
            //add the user to updateUserList
            else {
                UpdateUserDTO updateUserDTO=userMapper.toUpdateUserDto(usersDTO);
                updateUserList.add(updateUserDTO);
            }
        }
        //call updateUser method to update the user details
        allUsersList.addAll(updateUser(updateUserList));
        return allUsersList;
    }


    /**
     * Add a list of users based on the provided list of UsersDTO.
     *
     * @param createUsersDTO a list of CreateUsersDTO containing user details to be added.
     * @return a list of {@link UsersDTO} containing added users.
     * @throws DepartmentNotFoundException if any of the specified department does not exist.
     * @throws RoleNotFoundException if any of the specified role does not exist.
     * @throws UserEmailAlreadyExistsException if the specified EmailId already exists.
     * @throws UserEmpCodeAlreadyExistsException if the specified EmpCode already exists.
     */
    @Override
    public UsersDTO addUser(CreateUsersDTO createUsersDTO) throws DepartmentNotFoundException, RoleNotFoundException, UserEmailAlreadyExistsException, UserEmpCodeAlreadyExistsException {

        //Convert UserDTO to User Entity
        Users user = userMapper.toCreateUserEntity(createUsersDTO);

        //Check if Email already exist then throw an exception
        Users existingEmail=userRepository.findByUserEmailId(createUsersDTO.getUserEmailId());
        if (existingEmail != null) {
            log.warn("User  with email {} already exists",createUsersDTO.getUserEmailId());
            throw new UserEmailAlreadyExistsException();
        }

        //Check if User EmpCode already exists then throw an Exception
        Optional<Users> existingEmpcode=userRepository.findByempCode(createUsersDTO.getEmpCode());
        if(existingEmpcode.isPresent()){
            log.warn("User  with empcode {} already exists",createUsersDTO.getEmpCode());
            throw new UserEmpCodeAlreadyExistsException();
        }

        // Fetch given department names
        Set<Department> departments = departmentRepository.findAllByName(createUsersDTO.getDepartments());
        log.info("Department names received: {}", departments);
        if (departments.size() < createUsersDTO.getDepartments().size()) {
            throw new DepartmentNotFoundException();
        }
        user.setDepartments(departments);

        // Fetch given Role names
        Set<Role> roles = roleRepository.findAllByName(createUsersDTO.getRoles());
        if (roles.size() < createUsersDTO.getRoles().size()) {
            throw new RoleNotFoundException();
        }
        user.setRoles(roles);
        user.setCreationTime(LocalDateTime.now());

        // Save the user to the database
        Users savedUser = userRepository.save(user);
        log.info("User added successfully with id: {}", savedUser.getUserId());

        // Convert saved user entity to DTO and return
        return userMapper.toDto(savedUser);
    }


    /**
     * Update a list of users based on the provided list of UsersDTO.
     *
     * @param updateUserDtoList a list of UpdateUserDTO containing user details to be updated.
     * @return a list of {@link UsersDTO} containing updated users.
     * @throws UserNotFoundException if a user ID is not found in the repository.
     * @throws DepartmentNotFoundException if any of the specified department does not exist.
     * @throws RoleNotFoundException if any of the specified role does not exist.
     * @throws UserEmailAlreadyExistsException if the specified EmailId already exists.
     * @throws UserEmpCodeAlreadyExistsException if the specified EmpCode already exists.
     */
    @Override
    public List<UsersDTO> updateUser(List<UpdateUserDTO> updateUserDtoList) throws UserNotFoundException, DepartmentNotFoundException, RoleNotFoundException, UserEmailAlreadyExistsException, UserEmpCodeAlreadyExistsException {

        List<UsersDTO> allUpdateUserList=new ArrayList<>();

        for(UpdateUserDTO updateUserDTO: updateUserDtoList) {
            log.debug("Updating user with id: {}", updateUserDTO.getUserId());

            //Find existing user based on userId
            Users existingUser = userRepository.findById(updateUserDTO.getUserId()).orElseThrow(UserNotFoundException::new);

            //update status if new status is given
            if (updateUserDTO.getStatus() != null) {
                existingUser.setStatus(updateUserDTO.getStatus());
            }

            //update the departments for a user
            if (updateUserDTO.getDepartments() != null && !updateUserDTO.getDepartments().isEmpty()) {
                // Fetch all departments
                Set<Department> departments = departmentRepository.findAllByName(updateUserDTO.getDepartments());

                // Ensure the number of departments found matches the number in the DTO
                if (departments.size() < updateUserDTO.getDepartments().size()) {
                    throw new DepartmentNotFoundException();
                }

                existingUser.setDepartments(departments);
            }

            //update the roles for a user
            if (updateUserDTO.getRoles() != null && !updateUserDTO.getRoles().isEmpty()) {
                // Fetch all roles
                Set<Role> roles = roleRepository.findAllByName(updateUserDTO.getRoles());

                // Ensure the number of roles found matches the number in the DTO
                if (roles.size() < updateUserDTO.getRoles().size()) {
                    throw new RoleNotFoundException();
                }

                existingUser.setRoles(roles);
            }

            existingUser.setCreationTime(LocalDateTime.now());
            //Save the user details to the database
            Users updatedUser = userRepository.save(existingUser);

            //Map the updated user to UsersDTO
            UsersDTO updateUserDto = userMapper.toDto(updatedUser);

            allUpdateUserList.add(updateUserDto);
        }
        return allUpdateUserList;
    }


    /**
     * Retrieves a paginated list of filtered users records.
     *
     * @param userFilter UserFilterDTO containing the filtering criteria to be encapsulated.
     * @return a PaginatedResponse containing UsersDTO objects.
     */
    @Override
    public PaginatedResponse<UsersDTO> getFilterUsersPaginated(UserFilterDTO userFilter) {
        log.debug("Attempting to get users on the basis of filter");

        //Specification for filtering users based on provided criteria
        Specification<Users> spec = usersFilter(userFilter.getName(), userFilter.getDepartments(), userFilter.getRoles(), userFilter.getStatus());

        //Pageable object for pagination and sorting
        Pageable pageable =Pagination.createPageable(userFilter.getPage(),userFilter.getSize(),userFilter.getSortField(),userFilter.getSortOrder());

        //Retrieve paginated list of users
        Page<Users> userList = userRepository.findAll(spec, pageable);

        //Map User to UserDTO
        Page<UsersDTO> UserDTOPage = userList.map(userMapper::toDto);

        return Pagination.createdPaginatedContent(UserDTOPage);
    }


    /**
     * Create a Specification for filtering users based on various criteria.
     *
     * @param user the list of Username to filter by.
     * @param department the list of department to filter by.
     * @param role the list of role to filter by.
     * @param status the user status to filter by.
     * @return the specification for filtering user entity.
     */
    private Specification<Users> usersFilter(List<String> user, List<String> department, List<String> role, Boolean status) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            // Check if the username filter is provided
            if(user != null && !user.isEmpty()) {
                predicates.add(root.get("name").in(user));
                log.info("Added predicate for user: {}", user );
            }
            // Check if the department filter is provided
            if(department!= null && !department.isEmpty()) {
                predicates.add(root.get("departments").get("departmentName").in(department));
                log.info("Added predicate for department: {}", department);
            }
            // Check if the role filter is provided
            if(role !=null && !role.isEmpty()) {
                predicates.add(root.get("roles").get("roleName").in(role));
                log.info("Added predicate for role: {}", role);
            }
            // Check if the status filter is provided
            if(status!=null) {
                predicates.add(criteriaBuilder.equal(root.get("status"), status));
                log.info("Added predicate for status: {}", status);
            }

            query.distinct(true);

            //Combine all predicates into a single Predicate for the query
            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }


//    /**
//     * Sorts a page of user records based on the specified sortField and sortOrder.
//     *
//     * @param users the user page which needs to sort.
//     * @param sortField the field to sort by.
//     * @param sortOrder the order to sort, either ascending or descending.
//     * @param pageable the pageable information.
//     * @return a sorted security compliance page.
//     */
//    private Page<Users> sortList(Page<Users> users, String sortField, Boolean sortOrder, Pageable pageable) {
//        //Get the content of current page
//        List<Users> usersList = users.getContent();
//
//        // Comparator for sorting the users
//        Comparator<Users> comparator = (sc1, sc2) -> {
//            //Determine logic to compare fields based on sortField
//            switch (sortField) {
//                case "name":  // Sort by name
//                    return compareValues(sc1.getName(), sc2.getName());
//
//                default:
//                    throw new IllegalArgumentException("Invalid sort field: " + sortField);
//            }
//        };
//
//        // Reverse the comparator if sortOrder is descending
//        if (sortOrder) {
//            comparator = comparator.reversed();
//        }
//
//        // Sort and collect the list
//        List<Users> sortedList = usersList.stream()
//                .sorted(comparator)
//                .collect(Collectors.toList());
//
//        //Return a PageImpl containing the sorted list of users.
//        return new PageImpl<>(sortedList, pageable, users.getTotalElements());
//    }

//
//    /**
//     * Compares two Comparable values and handling nulls.
//     *
//     * @param value1 the value to compare.
//     * @param value2 the value to compare.
//     * @return a positive integer or a negative integer or zeo based on comparison.
//     */
//    private int compareValues(Comparable value1, Comparable value2) {
//        //If both value are null, then consider them equal
//        if (value1 == null && value2 == null) return 0;
//
//        //If value1 is null, consider value2 as greater
//        if (value1 == null) return -1;
//
//        //If value2 is null, consider value1 as greater
//        if (value2 == null) return 1;
//
//        //Compare two values
//        return value1.compareTo(value2);
//    }


    /**
     * Exports user records to an Excel.
     *
     * @param filter UserFilterDTO containing the filtering criteria to be encapsulated.
     * @return a ByteArrayResource containing the Excel file data.
     * @throws IOException if an error occurs during export.
     */
    @Override
    public ByteArrayResource exportExcelUsers(UserFilterDTO filter) throws IOException {
        log.info("Starting export of users data to Excel");

        // Retrieve all user DTOs
        PaginatedResponse<UsersDTO> paginatedResponse = getFilterUsersPaginated(filter);
        List<UsersDTO> usersList = paginatedResponse.getContent();
        log.debug("Fetched {} users records for export", usersList.size());

        // Output stream and configuration for Excel export
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        List<String> fieldsToInclude = List.of(
                "name",
                "departments",
                "roles",
                "status"
        );
        Map<String, String> customHeaders = new HashMap<>();
        customHeaders.put("name", "NAME");
        customHeaders.put("departments", "DEPARTMENTS");
        customHeaders.put("roles", "ROLES");
        customHeaders.put("status", "STATUS");

        // Use the existing export method
        exportExcel.exportToExcel(outputStream, usersList, "users", fieldsToInclude, customHeaders);
        log.info("Successfully exported user data to Excel");

        // Convert Output Stream to ByteArrayResource
        byte[] excelContent = outputStream.toByteArray();
        ByteArrayResource resource = new ByteArrayResource(excelContent);
        return resource;
    }
}