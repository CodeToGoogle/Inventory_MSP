package com.msp.inventory_manufacturing_service.repository;



import com.msp.inventory_manufacturing_service.entity.ManufacturingOrder;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
public interface MORepository extends JpaRepository<ManufacturingOrder,Integer> {
    List<ManufacturingOrder> findByOrderStatus(String status);
}

