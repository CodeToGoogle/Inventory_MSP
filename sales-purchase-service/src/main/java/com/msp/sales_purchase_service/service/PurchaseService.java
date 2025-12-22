package com.msp.sales_purchase_service.service;

import com.msp.sales_purchase_service.entity.PurchaseOrder;

public interface PurchaseService {
    PurchaseOrder createPurchaseOrder(PurchaseOrder purchaseOrder);
    void createPurchaseReceipt(Integer orderId);
}
