package com.iscp.backend.models;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "security_compliance")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class SecurityCompliance {

    @Id
    @GeneratedValue(generator = "custom-id-generator")
    @GenericGenerator(name = "custom-id-generator", strategy = "com.iscp.backend.components.CustomIdGenerator")
    @Column(name = "security_id", nullable = false)
    private String securityId;

    @Column(name="record_id",nullable = false)
    private String recordId;

    @Column(name="compliance_id", nullable = false)
    private String complianceId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "framework_id",nullable = false)
    private Framework framework;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "control_id",nullable = false)
    private Control control;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "checklist_id",nullable = false)
    private Checklist checklist;

    @ManyToMany
    @JoinTable(
            name = "security_compliance_department",
            joinColumns = @JoinColumn(name = "security_id"),
            inverseJoinColumns = @JoinColumn(name = "department_id")
    )
    private Set<Department> departments;


    @Column(name = "periodicity", nullable = false)
    private Enum.Periodicity periodicity;

    @Column(name = "evidence_compliance_status")
    private Boolean evidenceComplianceStatus = true;

    @Column(name = "evidence_comments",nullable = false)
    private String evidenceComments = "pending";

    @Column(name="creation_time", nullable = false)
    private LocalDateTime creationTime;

    @OneToMany(mappedBy = "securityCompliance", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Evidence> evidences;

}
