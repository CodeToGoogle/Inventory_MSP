package com.msp.inventory_manufacturing_service.repository;




import com.msp.inventory_manufacturing_service.entity.InventoryTransactions;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InventoryTransactionRepository extends JpaRepository<InventoryTransactions, Integer> {
}

