package com.msp.inventory_manufacturing_service.entity;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name="ManufacturingOrders")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class ManufacturingOrder {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer orderID;
    private String orderNumber;
    private Integer referenceSO;
    private LocalDate orderDate;
    private Integer productID;
    @Column(precision=15,scale=3)
    private BigDecimal totalQty;
    private Integer unitID;
    private String orderStatus; // Draft, Planned, Released, In Progress, Partially Completed, Completed
    private String deliveryStatus;
    private LocalDate startDate;
    private LocalDate completionDate;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    @OneToMany(mappedBy="manufacturingOrder", cascade=CascadeType.ALL, orphanRemoval=true)
    private List<ManufacturingOrderLine> lines;
    @PrePersist public void prePersist(){ createdAt = LocalDateTime.now(); updatedAt=createdAt; }
    @PreUpdate public void preUpdate(){ updatedAt = LocalDateTime.now(); }
}

