package com.iscp.backend.models;


import com.iscp.backend.components.CustomIdGenerator;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.GenericGenerator;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;


@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
@Entity
@Table(name = "checklist")
public class Checklist {

    @Id
    @GeneratedValue(generator = "custom-id")
    @GenericGenerator(name = "custom-id", type = CustomIdGenerator.class)
    @Column(name = "checklist_id", unique = true, nullable = false)
    private String checklistId;

    @Column(name = "control_checklist", nullable = false)
    private String controlChecklist;

    @Column(name = "description")
    private String description;

    @Column(name = "status")
    private Boolean status=true;

    @Column(name="creation_time", nullable = false)
    private LocalDateTime creationTime;

    @OneToMany(mappedBy = "checklist", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<SecurityCompliance> securityCompliance;


//    @ManyToMany(mappedBy = "checklists")
//    private Set<SecurityCompliance> securityCompliance;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "control_id" ,nullable = false)
    private Control control;

    @OneToMany(mappedBy = "checklist", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Evidence> evidences;

}
