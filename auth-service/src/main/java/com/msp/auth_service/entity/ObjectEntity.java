package com.msp.auth_service.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "Objects")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class ObjectEntity {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer objectID;

    private String objectType;

    private Integer objectParent;

    @Column(nullable=false)
    private String objectName;

    private String objectPath;
}
