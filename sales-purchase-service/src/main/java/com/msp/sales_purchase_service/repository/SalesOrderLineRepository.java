package com.msp.sales_purchase_service.repository;

import com.msp.sales_purchase_service.entity.SalesOrderLine;
import org.springframework.data.jpa.repository.JpaRepository;

import java.math.BigDecimal;
import java.util.List;

public interface SalesOrderLineRepository extends JpaRepository<SalesOrderLine, Integer> {
    List<SalesOrderLine> findByOrderOrderIdAndDeliveredQtyLessThan(Integer orderId, BigDecimal orderedQty);
}
