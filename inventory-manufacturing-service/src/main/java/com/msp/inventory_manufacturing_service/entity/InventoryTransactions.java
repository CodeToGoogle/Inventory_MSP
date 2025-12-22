package com.msp.inventory_manufacturing_service.entity;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name="InventoryTransactions")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class InventoryTransactions {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer transactionID;
    private String transactionNumber;
    private LocalDateTime transactionDate;
    private Integer operationTypeID;
    private Integer fromLocationID;
    private Integer toLocationID;
    private String referenceType;
    private Integer referenceNumber;
    @Column(precision=15,scale=3)
    private BigDecimal totalQty;
    @OneToMany(mappedBy="transaction", cascade=CascadeType.ALL)
    private List<InventoryTransactionsLine> lines;
}
