package com.msp.sales_purchase_service.repository;

import com.msp.sales_purchase_service.entity.PurchaseOrderLine;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PurchaseOrderLineRepository extends JpaRepository<PurchaseOrderLine,Integer> { }

