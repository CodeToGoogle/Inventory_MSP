package com.msp.sales_purchase_service.repository;

import com.msp.sales_purchase_service.entity.SalesOrder;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SalesOrderRepository extends JpaRepository<SalesOrder, Integer> {
}
