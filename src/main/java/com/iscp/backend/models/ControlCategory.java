package com.iscp.backend.models;


import com.iscp.backend.components.CustomIdGenerator;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.GenericGenerator;

import java.util.List;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString(exclude = "controlList")
@Entity
@Table(name = "control_category")
public class ControlCategory {
    @Id
    @GeneratedValue(generator = "custom-id")
    @GenericGenerator(name = "custom-id", type = CustomIdGenerator.class)
    @Column(name = "control_category_id", unique = true, nullable = false)
    private String controlCategoryId;


    @Column(name = "control_category_name", nullable = false)
    private String controlCategoryName;

    @OneToMany(mappedBy = "controlCategory", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Control> controlList;
}
