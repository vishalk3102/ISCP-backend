package com.iscp.backend.models;

import com.iscp.backend.components.CustomIdGenerator;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name="users")
public class Users {
    @Id
    @GeneratedValue(generator = "custom-id")
    @GenericGenerator(name = "custom-id", type = CustomIdGenerator.class)
    @Column(name = "user_id", unique = true, nullable = false)
    private String userId;

    @Column(name="name", nullable = false)
    private String name;

//    @Column(name="password", nullable = false)
//    private String password;

    @Column(name="status", nullable = false)
    private Boolean status=true;

    @Column(name="emp_code")
    private String empCode;

    @Column(name = "user_email_Id")
    private String userEmailId;

    @ManyToMany(cascade = {CascadeType.MERGE}, fetch = FetchType.EAGER)
    @JoinTable(name="user_department", joinColumns = @JoinColumn(name="user_id"),inverseJoinColumns = @JoinColumn(name = "department_id"))
    private Set<Department> departments;

    @ManyToMany(cascade = {CascadeType.MERGE}, fetch = FetchType.EAGER)
    @JoinTable(name = "user_role", joinColumns = @JoinColumn(name = "user_id"), inverseJoinColumns = @JoinColumn(name = "role_id"))
    private Set<Role> roles;

//    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
//    private List<SecurityCompliance> securityCompliance;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Evidence> evidences;

    @Column(name="creation_time", nullable = false)
    private LocalDateTime creationTime;

}
