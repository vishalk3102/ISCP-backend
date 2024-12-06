package com.iscp.backend.models;

import com.iscp.backend.components.CustomIdGenerator;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;

import java.sql.Timestamp;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name="evidence")
public class Evidence {
    @Id
    @GeneratedValue(generator = "custom-id")
    @GenericGenerator(name = "custom-id", type = CustomIdGenerator.class)
    @Column(name = "evidence_id", unique = true, nullable = false)
    private String evidenceId;

    @Column(name="file_name", nullable = false)
    private String fileName;

    @Column(name="file_type", nullable = false)
    private String fileType;

    @Column(name = "timestamp")
    private Timestamp timestamp;

    @Column(name="file_reference", nullable = false)
    private String fileReference;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "checklist_id", nullable = false)
    private Checklist checklist;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private Users user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "security_id", nullable = false)
    private SecurityCompliance securityCompliance;
}
