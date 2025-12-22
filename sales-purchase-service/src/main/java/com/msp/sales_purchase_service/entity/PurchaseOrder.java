package com.msp.sales_purchase_service.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "PurchaseOrders")
@Data
public class PurchaseOrder {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "OrderID")
    private Integer orderId;

    @Column(name = "OrderNumber", nullable = false, unique = true)
    private String orderNumber;

    @Column(name = "OrderDate", nullable = false)
    private LocalDate orderDate;

    @Column(name = "VendorID", nullable = false)
    private Integer vendorId;

    @Column(name = "OrderStatus")
    private String orderStatus;

    @Column(name = "DeliveryStatus")
    private String deliveryStatus;

    @Column(name = "TotalAmount")
    private BigDecimal totalAmount;

    @Column(name = "TotalQty")
    private BigDecimal totalQty;

    @Column(name = "CreatedAt", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "UpdatedAt")
    private LocalDateTime updatedAt;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<PurchaseOrderLine> lines;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
