package com.msp.inventory_manufacturing_service.repository;


import com.msp.inventory_manufacturing_service.entity.ManufacturingOrderLine;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
public interface MOLineRepository extends JpaRepository<ManufacturingOrderLine,Integer> {
    List<ManufacturingOrderLine> findByManufacturingOrderOrderID(Integer orderId);
}

