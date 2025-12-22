package com.msp.inventory_manufacturing_service.repository;

import com.msp.inventory_manufacturing_service.entity.WorkOrder;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
public interface WorkOrderRepository extends JpaRepository<WorkOrder,Integer> {
    List<WorkOrder> findByReferenceMO(Integer moId);
    List<WorkOrder> findByWcStatus(String status);
}

