package com.msp.sales_purchase_service.repository;

import com.msp.sales_purchase_service.entity.PurchaseOrder;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PurchaseOrderRepository extends JpaRepository<PurchaseOrder, Integer> {
}
