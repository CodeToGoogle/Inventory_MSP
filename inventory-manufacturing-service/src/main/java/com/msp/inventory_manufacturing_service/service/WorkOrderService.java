package com.msp.inventory_manufacturing_service.service;

import java.util.Map;

public interface WorkOrderService {
    Map<String,Object> startWorkOrder(Integer woId, String idempotencyKey);
    Map<String,Object> completeWorkOrder(Integer woId, String idempotencyKey);
}

