package com.msp.sales_purchase_service.service;

import com.msp.sales_purchase_service.entity.PurchaseOrder;
import com.msp.sales_purchase_service.repository.PurchaseOrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PurchaseServiceImpl implements PurchaseService {

    private final PurchaseOrderRepository purchaseOrderRepository;
    private final KafkaTemplate<String, Object> kafkaTemplate;

    @Override
    public PurchaseOrder createPurchaseOrder(PurchaseOrder purchaseOrder) {
        return purchaseOrderRepository.save(purchaseOrder);
    }

    @Override
    public void createPurchaseReceipt(Integer orderId) {
        PurchaseOrder purchaseOrder = purchaseOrderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("PurchaseOrder not found with id: " + orderId));
        // Here you would typically have more complex logic to create a GRN
        // For now, we'll just publish an event
        kafkaTemplate.send("purchase.grn.created", purchaseOrder);
    }
}
