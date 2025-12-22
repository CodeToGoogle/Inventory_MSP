package com.msp.inventory_manufacturing_service.entity;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;

@Entity
@Table(name="InventoryTransactionsLine")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class InventoryTransactionsLine {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer transactionLineID;
    @ManyToOne
    @JoinColumn(name="TransactionID")
    private InventoryTransactions transaction;
    private Integer productID;
    private Integer batchID;
    @Column(precision=15,scale=3)
    private BigDecimal qty;
    private Integer unitID;
    private BigDecimal unitCost;
}
