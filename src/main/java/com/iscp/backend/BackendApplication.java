package com.iscp.backend;

import com.iscp.backend.models.*;
import com.iscp.backend.models.Enum;
import com.iscp.backend.repositories.DepartmentRepository;
import com.iscp.backend.repositories.PermissionRepository;
import com.iscp.backend.repositories.RoleRepository;
import com.iscp.backend.repositories.UsersRepository;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;


import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

@SpringBootApplication
@SecurityScheme(
		name = "bearerAuth",
		type = SecuritySchemeType.HTTP,
		scheme = "bearer",
		bearerFormat = "JWT"
)
@AllArgsConstructor
@NoArgsConstructor
@EnableAutoConfiguration(exclude = {DataSourceAutoConfiguration.class})
public class BackendApplication extends SpringBootServletInitializer implements CommandLineRunner {

	@Autowired
	private  DepartmentRepository departmentRepository;
	@Autowired
	private  RoleRepository roleRepository;
	@Autowired
	private  PermissionRepository permissionRepository;
	@Autowired
	private  UsersRepository usersRepository;
	@Autowired
	private  BCryptPasswordEncoder passwordEncoder;

	@Override
	protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
		return application.sources(BackendApplication.class);
	}


	public static void main(String[] args) {
		SpringApplication.run(BackendApplication.class, args);
	}
	@Override
	public void run(String... args) throws Exception {

		//Creating Departments
		for (Enum.DepartmentType departmentType : Enum.DepartmentType.values()) {
			if (!departmentRepository.existByDepartmentName(departmentType)) {
				Department department = new Department();
				department.setDepartmentName(departmentType);
				departmentRepository.save(department);
			}

		}

		//Creating Roles
		Optional<Role> adminRole = createRoleIfNotExists(Enum.RoleType.Admin);
		Optional<Role> uploaderRole = createRoleIfNotExists(Enum.RoleType.Uploader);
		Optional<Role> viewerRole = createRoleIfNotExists(Enum.RoleType.Viewer);

		//Creating Permissions
		Permission adminPermission = createPermissionIfNotExists(Enum.PermissionType.Admin);
		Permission uploaderPermission = createPermissionIfNotExists(Enum.PermissionType.Uploader);
		Permission viewerPermission = createPermissionIfNotExists(Enum.PermissionType.Viewer);


		//Mapping permissions to roles
		mapPermissionToRole(adminRole,Set.of(adminPermission,uploaderPermission,viewerPermission));
		mapPermissionToRole(uploaderRole,Set.of(uploaderPermission,viewerPermission));
		mapPermissionToRole(viewerRole,Set.of(viewerPermission));


		//Create Users
		createUserIfNotExists("sunilk@contata.in","Sunil Kaim", "sk1234","Emp001", Set.of(Enum.DepartmentType.Administration), Set.of(Enum.RoleType.Admin));
		createUserIfNotExists("shilpia@contata.in","Shilpi Agarwal", "sa1234","Emp002", Set.of(Enum.DepartmentType.Human_Resource), Set.of(Enum.RoleType.Viewer));
		createUserIfNotExists("pranavv@contata.in","Pranav Vishnoi", "p1234","Emp003", Set.of(Enum.DepartmentType.SysAdmin), Set.of(Enum.RoleType.Uploader));
	}


	//FUNCTION TO CREATE USERS
	private void createUserIfNotExists(String userEmailId, String name, String password, String empCode, Set<Enum.DepartmentType> departmentTypes, Set<Enum.RoleType> roleTypes) {
		if (!usersRepository.existByUserEmailId(userEmailId)) {
			Users user = new Users();
			user.setUserEmailId(userEmailId);
			user.setName(name);
//			user.setPassword(passwordEncoder.encode(password));
			user.setEmpCode(empCode);

			Set<Department> departments = new HashSet<>();
			for (Enum.DepartmentType departmentType : departmentTypes) {
				Optional<Department> department = departmentRepository.findByDepartmentName(departmentType);
				department.ifPresent(departments::add);
			}
			user.setDepartments(departments);

			// Fetch and set roles
			Set<Role> roles = new HashSet<>();
			for (Enum.RoleType roleType : roleTypes) {
				Optional<Role> role = roleRepository.findByRoleName(roleType);
				role.ifPresent(roles::add);
			}
			user.setRoles(roles);
			user.setCreationTime(LocalDateTime.now());
			usersRepository.save(user);
		}
	}

	//FUNCTION TO CREATE ROLE
	private Optional<Role> createRoleIfNotExists(Enum.RoleType roleType)
	{
		return Optional.of(roleRepository.findByRoleName(roleType).orElseGet(() -> {
			Role role = new Role();
			role.setRoleName(roleType);
			return roleRepository.save(role);
		}));
	}

	//FUNCTION TO CREATE PERMISSION
	private Permission createPermissionIfNotExists(Enum.PermissionType permissionType)
	{
		return permissionRepository.findByRolePermissions(permissionType).orElseGet(()-> {
			Permission permission = new Permission();
			permission.setRolePermissions(permissionType);
			return permissionRepository.save(permission);
		});
	}

	//FUNCTION TO MAP PERMISSION TO ROLE
	private void mapPermissionToRole(Optional<Role> roleOptional, Set<Permission> permissionSet)
	{
		roleOptional.ifPresent(role-> {
			role.setPermissions(permissionSet);
			roleRepository.save(role);
		});
	}
}