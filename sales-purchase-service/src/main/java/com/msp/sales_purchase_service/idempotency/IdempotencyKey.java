package com.msp.sales_purchase_service.idempotency;



import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;

@Entity
@Table(name = "IdempotencyKeys", indexes = {
        @Index(name = "ux_idempotency_key_idx", columnList = "idempotencyKey")
})
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class IdempotencyKey {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idempotencyID;

    @Column(nullable = false, length = 255, unique = true)
    private String idempotencyKey;

    @Column(length = 16)
    private String requestMethod;

    @Column(length = 1000)
    private String requestPath;

    @Lob
    @Column(columnDefinition = "LONGTEXT")
    private String requestBody;

    @Lob
    @Column(columnDefinition = "LONGTEXT")
    private String responseBody;

    private Integer responseStatus;

    private Instant createdAt;
    private Instant updatedAt;

    @PrePersist
    public void prePersist() {
        createdAt = Instant.now();
        updatedAt = createdAt;
    }

    @PreUpdate
    public void preUpdate() {
        updatedAt = Instant.now();
    }
}
