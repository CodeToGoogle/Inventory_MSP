package com.msp.inventory_manufacturing_service.repository;

import com.msp.inventory_manufacturing_service.entity.BillOfMaterialsLine;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface BOMLineRepository extends JpaRepository<BillOfMaterialsLine, Integer> {
    List<BillOfMaterialsLine> findByBillOfMaterials_BomID(Integer bomId);
}
