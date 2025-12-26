package com.msp.sales_purchase_service.entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "SalesOrders")
@Data
public class SalesOrder {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "OrderID")
    private Integer orderId;

    @Column(name = "OrderNumber", nullable = false, unique = true)
    private String orderNumber;

    @Column(name = "OrderDate", nullable = false)
    private LocalDate orderDate;

    @Column(name = "CustomerID", nullable = false)
    private Integer customerId;

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

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true) // Changed to LAZY
    @JsonManagedReference
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private List<SalesOrderLine> lines = new ArrayList<>();

    public void addLine(SalesOrderLine line) {
        this.lines.add(line);
        line.setOrder(this);
    }

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        if (orderDate == null) {
            orderDate = LocalDate.now();
        }
        if (orderNumber == null) {
            orderNumber = "SO-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        }
        if (orderStatus == null) {
            orderStatus = "Draft";
        }
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
