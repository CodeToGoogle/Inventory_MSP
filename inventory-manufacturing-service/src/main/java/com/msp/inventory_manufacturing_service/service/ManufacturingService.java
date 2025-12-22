package com.msp.inventory_manufacturing_service.service;

import com.msp.inventory_manufacturing_service.dto.CreateMORequest;

import java.util.Map;

import java.util.Map;
public interface ManufacturingService {
    Map<String,Object> createMO(CreateMORequest req, String idempotencyKey);
}


