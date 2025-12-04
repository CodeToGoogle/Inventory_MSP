package com.msp.auth_service.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "ObjectPermissions",
        uniqueConstraints = @UniqueConstraint(columnNames = {"roleID","objectID"}))
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class ObjPermission {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer permissionID;

    private Integer roleID;
    private Integer objectID;

    private Boolean access = false;
    private Boolean edit = false;
    private Boolean delete = false;
    private Boolean print = false;
    private Boolean attach = false;
    private Boolean addNotes = false;
    private Boolean approve = false;
    private Boolean reject = false;
    private Boolean reset = false;
}
