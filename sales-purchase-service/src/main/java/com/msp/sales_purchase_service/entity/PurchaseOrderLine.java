package com.msp.sales_purchase_service.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.math.BigDecimal;

@Entity
@Table(name = "PurchaseOrderLines")
@Data
public class PurchaseOrderLine {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "LineID")
    private Integer lineId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "OrderID", nullable = false)
    private PurchaseOrder order;

    @Column(name = "ProductID", nullable = false)
    private Integer productId;

    @Column(name = "OrderedQty", nullable = false)
    private BigDecimal orderedQty;

    @Column(name = "UnitID", nullable = false)
    private Integer unitId;

    @Column(name = "PurchaseUnitPrice", nullable = false)
    private BigDecimal purchaseUnitPrice;

    @Column(name = "LineTotal", insertable = false, updatable = false)
    private BigDecimal lineTotal;

    @Column(name = "DeliveredQty")
    private BigDecimal deliveredQty;
}
