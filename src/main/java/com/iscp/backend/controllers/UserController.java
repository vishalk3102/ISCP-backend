package com.iscp.backend.controllers;

import com.iscp.backend.dto.*;
import com.iscp.backend.exceptions.*;
import com.iscp.backend.services.UsersService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

/**
 * Controller for handling user-related operations.
 */
@RestController
@RequestMapping(path = UserController.PATH, produces = MediaType.APPLICATION_JSON_VALUE)
@AllArgsConstructor
@Slf4j
public class UserController {

    private final UsersService usersService;

    public final static String PATH = "/api/users";

    /**
     * Retrieves a list of all users.
     *
     * @return a ResponseEntity containing a list of UsersDTO.
     */
    @Operation(summary = "Get all users", description = "Get list of all users from the database")
    @ApiResponses(value = {@ApiResponse(responseCode = "201",description = "list of users",content = {
            @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,schema = @Schema(implementation = UsersDTO.class))}),
            @ApiResponse(responseCode = "204", description = "No User found", content = @Content),
            @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content)
    })
    @GetMapping("/get-all-users")
    public ResponseEntity<List<UsersDTO>> getAllUser() {
        log.info("Received request to get all user details");

        //call getALlUsers to fetch users list.
        List<UsersDTO> usersList = usersService.getAllUsers();

        if (usersList.isEmpty()) {
            log.info("No user found");
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        }

        log.info("{} User details fetched successfully", usersList.size());
        return ResponseEntity.status(HttpStatus.OK).body(usersList);
    }


    /**
     * Add or update a list of users in the database.
     *
     * @param createUsersDTOList a list of CreateUsersDTO containing details of users to be added or updated.
     * @return a ResponseEntity containing a list of UsersDTO.
     * @throws UserNotFoundException if a user ID is not found in the repository.
     * @throws DepartmentNotFoundException if any of the specified department does not exist.
     * @throws RoleNotFoundException if any of the specified role does not exist.
     * @throws UserEmailAlreadyExistsException  if a user EmailId already exists.
     * @throws UserEmpCodeAlreadyExistsException if a user EmpCode already exists.
     */
    @Operation(summary = "Add a new user or update an existing user", description = "Add a new user or Update an existing user entry in the database")
    @ApiResponses(value = {@ApiResponse(responseCode = "201",description = "User updated",content = {
            @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,schema = @Schema(implementation = UsersDTO.class))}),
            @ApiResponse(responseCode = "400",description = "User Not Found",content = @Content),
            @ApiResponse(responseCode = "404",description = "Role Not Found",content = @Content),
            @ApiResponse(responseCode = "407",description = "Department Not Found",content = @Content),
            @ApiResponse(responseCode = "408", description = "User Email Already Exists", content = @Content),
            @ApiResponse(responseCode = "409", description = "User EmpCode Already Exists", content = @Content),
            @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content)
    })
    @PostMapping("/add-edit-users")
    @PreAuthorize("hasRole('Admin')")
    public ResponseEntity<List<UsersDTO>> addEditUsers(@RequestBody List<CreateUsersDTO> createUsersDTOList) throws UserNotFoundException, DepartmentNotFoundException, RoleNotFoundException, UserEmailAlreadyExistsException, UserEmpCodeAlreadyExistsException {
        log.info("Received request to Add or Update user");

        //call addUpdateUsers method in service
        List<UsersDTO> usersDTOList = usersService.addUpdateUsers(createUsersDTOList);

        log.info("user added or updated successfully");
        return ResponseEntity.status(HttpStatus.CREATED).body(usersDTOList);
    }


    /**
     * Retrieves a paginated list of filtered user records based on provided filters.
     *
     * @param userFilter UserFilterDTO containing the filtering criteria to be encapsulated.
     * @return a PaginatedResponse containing UsersDTO objects.
     */
    @Operation(summary = "Get paginated users using filters", description = "Fetch users using filters from the database as per pagination")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Paginated Users fetched successfully",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            array = @ArraySchema(schema = @Schema(implementation = UsersDTO.class)))),
            @ApiResponse(responseCode = "204", description = "No Users found", content = @Content),
            @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content)
    })
    @PostMapping("/get-filtered-users-paginated")
    public ResponseEntity<PaginatedResponse<UsersDTO>> getAllUsersFiltered(@RequestBody UserFilterDTO userFilter) {
        log.info("Received request to get all Users");

        //call getFilterUsersPaginated method to retrieve paginated users records based on filters
        PaginatedResponse<UsersDTO> usersFilterPaginated = usersService.getFilterUsersPaginated(userFilter);

        log.info("All Users  fetched successfully:{}", usersFilterPaginated.getTotalElements());
        return ResponseEntity.status(HttpStatus.OK).body(usersFilterPaginated);
    }


    /**
     * Exports user records to an Excel.
     *
     * @param filter UserFilterDTO containing the filtering criteria to be encapsulated.
     * @return a ResponseEntity containing a ByteArrayResource representing the Excel.
     * @throws IOException if an error occurs during export.
     */
    @Operation(summary = "To export data to excel sheet", description = "To export table data to excel sheet of Users")
    @PostMapping("/export-excel")
    public ResponseEntity<ByteArrayResource> exportsExcelUsers(@RequestBody UserFilterDTO filter) throws IOException {
        log.info("Received request to export users table data to excel sheet");

        //Export the fetched data to an Excel
        ByteArrayResource resource = usersService.exportExcelUsers(filter);
        log.info("Exported users data to Excel successfully");

        String fileName = "users.xlsx";

        return ResponseEntity.status(HttpStatus.OK)
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment;filename=" + fileName)
                .contentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                .contentLength(resource.contentLength())
                .body(resource);
    }
}