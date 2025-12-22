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

    @Column(name = "`access`")
    @Builder.Default
    private Boolean canAccess = false;
    @Column(name = "`edit`")
    @Builder.Default
    private Boolean canEdit = false;
    @Column(name = "`delete`")
    @Builder.Default
    private Boolean canDelete = false;
    @Column(name = "`print`")
    @Builder.Default
    private Boolean canPrint = false;
    @Column(name = "`attach`")
    @Builder.Default
    private Boolean canAttach = false;
    @Column(name = "`addNotes`")
    @Builder.Default
    private Boolean canAddNotes = false;
    @Column(name = "`approve`")
    @Builder.Default
    private Boolean canApprove = false;
    @Column(name = "`reject`")
    @Builder.Default
    private Boolean canReject = false;
    @Column(name = "`reset`")
    @Builder.Default
    private Boolean canReset = false;
}
