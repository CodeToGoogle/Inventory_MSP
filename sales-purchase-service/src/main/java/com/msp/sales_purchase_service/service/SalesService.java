package com.msp.sales_purchase_service.service;

import com.msp.sales_purchase_service.dto.CreateSalesOrderRequest;
import com.msp.sales_purchase_service.dto.SalesOrderResponse;

public interface SalesService {
    SalesOrderResponse createSalesOrder(CreateSalesOrderRequest request, String idempotencyKey);
    SalesOrderResponse approveSalesOrder(Integer orderId);
}
