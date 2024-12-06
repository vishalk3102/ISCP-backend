package com.iscp.backend.models;

import com.iscp.backend.components.CustomIdGenerator;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.GenericGenerator;

import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name="department")
public class Department {
    @Id
    @GeneratedValue(generator = "custom-id")
    @GenericGenerator(name = "custom-id", type = CustomIdGenerator.class)
    @Column(name = "department_id", unique = true, nullable = false)
    private String departmentId;

    @Enumerated(EnumType.STRING)
    @Column(name="department_name", nullable = false)
    private Enum.DepartmentType departmentName;

    @ManyToMany(mappedBy = "departments", fetch = FetchType.LAZY)
    private Set<Users> users = new HashSet<>();

    @ManyToMany(mappedBy = "departments", fetch = FetchType.LAZY)
    private Set<SecurityCompliance> securityCompliance = new HashSet<>();


}
