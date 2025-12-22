package com.msp.sales_purchase_service.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.Data;
import java.math.BigDecimal;

@Entity
@Table(name = "SalesOrderLines")
@Data
public class SalesOrderLine {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "LineID")
    private Integer lineId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "OrderID", nullable = false)
    @JsonBackReference
    private SalesOrder order;

    @Column(name = "ProductID", nullable = false)
    private Integer productId;

    @Column(name = "OrderedQty", nullable = false)
    private BigDecimal orderedQty;

    @Column(name = "UnitID", nullable = false)
    private Integer unitId;

    @Column(name = "SaleUnitPrice", nullable = false)
    private BigDecimal saleUnitPrice;

    @Column(name = "LineTotal", insertable = false, updatable = false)
    private BigDecimal lineTotal;

    @Column(name = "DeliveredQty")
    private BigDecimal deliveredQty;
}
