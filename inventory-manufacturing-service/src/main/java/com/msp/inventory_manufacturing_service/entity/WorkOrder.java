package com.msp.inventory_manufacturing_service.entity;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name="WorkOrders")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class WorkOrder {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer workOrderID;
    private String workOrderNumber;
    private Integer referenceMO;
    private Integer workcenterID;
    private Integer operationID;
    private Integer productID;
    @Column(precision=15,scale=3)
    private BigDecimal orderedQty;
    private Integer unitID;
    @Column(precision=15,scale=3)
    private BigDecimal deliveredQty = BigDecimal.ZERO;
    private String wcStatus; // Pending, In Progress, Completed, On Hold
    private LocalDateTime scheduledStart;
    private LocalDateTime scheduledEnd;
    private LocalDateTime actualStart;
    private LocalDateTime actualEnd;
    private LocalDateTime createdAt;
    @PrePersist public void prePersist(){ createdAt = LocalDateTime.now(); }
}

