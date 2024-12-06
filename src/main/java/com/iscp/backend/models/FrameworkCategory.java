package com.iscp.backend.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;

import java.util.List;

@Entity
@Table(name = "framework_category")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class FrameworkCategory {

    @Id
    @GeneratedValue(generator = "custom-id-generator")
    @GenericGenerator(name = "custom-id-generator", strategy = "com.iscp.backend.components.CustomIdGenerator")
    @Column(name = "framework_category_id", nullable = false)
    private String frameworkCategoryId;

    @Column(name = "framework_category_name", nullable = false, unique = true)
    private String frameworkCategoryName;

    @OneToMany(mappedBy = "frameworkCategory", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Framework> frameworks;

}
