package com.iscp.backend.models;


import com.iscp.backend.components.CustomIdGenerator;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.GenericGenerator;

import java.time.LocalDateTime;
import java.util.List;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString(exclude = {"controlCategory", "securityCompliance", "checklists"})
@Entity
@Table(name = "controls")
public class Control {
    @Id
    @GeneratedValue(generator = "custom-id")
    @GenericGenerator(name = "custom-id", type = CustomIdGenerator.class)
    @Column(name = "control_id", unique = true, nullable = false)
    private String controlId;

    @Column(name = "control_name", nullable = false)
    private String controlName;

    @Column(name = "description")
    private String description;

    @Column(name = "status")
    private Boolean status=true;

    @Column(name="creation_time", nullable = false)
    private LocalDateTime creationTime;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "control_category_id",nullable = false)
    private ControlCategory controlCategory;

    @OneToMany(mappedBy = "control", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<SecurityCompliance> securityCompliance;

    @OneToMany(mappedBy = "control",cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Checklist> checklists;
}
