package com.msp.auth_service.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "UserRoles",
        uniqueConstraints = @UniqueConstraint(columnNames = {"userID","roleID"}))
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class UserRole {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer recID;

    private Integer userID;
    private Integer roleID;

    private LocalDateTime assignedAt;

    @PrePersist
    public void prePersist() {
        assignedAt = LocalDateTime.now();
    }
}
