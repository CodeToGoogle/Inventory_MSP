package com.msp.inventory_manufacturing_service.entity;



import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;

@Entity
@Table(name="ManufacturingOrderLines")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class ManufacturingOrderLine {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer lineID;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="OrderID")
    private ManufacturingOrder manufacturingOrder;
    private Integer productID; // finished or component
    @Column(precision=15,scale=3)
    private BigDecimal orderedQty;
    private Integer unitID;
    @Column(precision=15,scale=3)
    private BigDecimal deliveredQty = BigDecimal.ZERO;
}

