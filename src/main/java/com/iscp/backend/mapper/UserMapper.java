package com.iscp.backend.mapper;

import com.iscp.backend.dto.CreateUsersDTO;
import com.iscp.backend.dto.UpdateUserDTO;
import com.iscp.backend.dto.UsersDTO;
import com.iscp.backend.models.Department;
import com.iscp.backend.models.Role;
import com.iscp.backend.models.Users;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Mapper interface for mapping between User entities and UserDTO.
 */
@Mapper(componentModel = "spring")
public interface UserMapper {

    /**
     * Mapping Users Entity to UsersDTO.
     *
     * @param user the user entity to convert.
     * @return the converted UsersDTO.
     */
    @Mapping(target = "departments", source = "departments", qualifiedByName = "departmentToDepartmentName")
    @Mapping(target = "roles", source = "roles", qualifiedByName = "roleToRoleName")
    UsersDTO toDto(Users user);

    //Custom mapping for Department to String (department name)
    @Named("departmentToDepartmentName")
    static Set<String> mapDepartmentsToNames(Set<Department> departments) {
        return departments.stream()
                .map(department -> department.getDepartmentName().toString())
                .collect(Collectors.toSet());
    }

    //Custom mapping for Role to String (role name)
    @Named("roleToRoleName")
    static Set<String> mapRolesToNames(Set<Role> roles) {
        return roles.stream()
                .map(role -> role.getRoleName().toString())
                .collect(Collectors.toSet());
    }


    /**
     * Mapping CreateUsersDTO to Users Entity.
     *
     * @param createUsersDTO the user DTO to convert.
     * @return the converted user Entity.
     */
    @Mapping(target = "departments", ignore = true)
    @Mapping(target = "roles", ignore = true)
    Users toCreateUserEntity(CreateUsersDTO createUsersDTO);


    /**
     * Mapping List of Users Entity to List of UserDTO.
     *
     * @param users the list of user Entity to convert.
     * @return the converted list of UserDTO.
     */
    List<UsersDTO> DTO_LIST(List<Users> users);


    /**
     * Mapping CreateUsersDTO to UpdateUserDTO.
     *
     * @param usersDTO the user DTO to convert.
     * @return the converted UserDTO.
     */
    UpdateUserDTO toUpdateUserDto(CreateUsersDTO usersDTO);


    /**
     * Mapping UpdateUserDTO to Users.
     *
     * @param updateUserDTO the user DTO to convert.
     * @return the converted user Entity.
     */
    @Mapping(target = "departments", ignore = true)
    @Mapping(target = "roles", ignore = true)
    @Mapping(target = "evidences", ignore = true)
    Users toUpdateUserEntity(UpdateUserDTO updateUserDTO);
}
