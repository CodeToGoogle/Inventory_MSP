package com.msp.inventory_manufacturing_service.repository;

import com.msp.inventory_manufacturing_service.entity.BillOfMaterials;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface BillOfMaterialsRepository extends JpaRepository<BillOfMaterials, Integer> {
    List<BillOfMaterials> findByProductIdAndIsActive(Integer productId, boolean isActive);
    Optional<BillOfMaterials> findByProductIdAndIsActiveTrue(Integer productId);
}
