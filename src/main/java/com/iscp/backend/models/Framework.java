package com.iscp.backend.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;

import java.sql.Date;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "framework")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Framework {

    @Id
    @GeneratedValue(generator = "custom-id-generator")
    @GenericGenerator(name = "custom-id-generator", strategy = "com.iscp.backend.components.CustomIdGenerator")
    @Column(name = "framework_id", nullable = false)
    private String frameworkId;

    @Column(name = "framework_name", nullable = false, unique = true)
    private String frameworkName;

    @Column(name = "start_date", nullable = false)
    private String startDate;

    @Column(name = "end_date", nullable = false)
    private String endDate;

    @Column(name = "description", nullable = false)
    private String description;

    @Column(name = "status", nullable = false)
    private Boolean status = true;


    @Column(name="creation_time", nullable = false)
    private LocalDateTime creationTime;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "framework_category_id",nullable = false)
    private FrameworkCategory frameworkCategory;

    @OneToMany(mappedBy = "framework", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<SecurityCompliance> securityCompliance;

}
