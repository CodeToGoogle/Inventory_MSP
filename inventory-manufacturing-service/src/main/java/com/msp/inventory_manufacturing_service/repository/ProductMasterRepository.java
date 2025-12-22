package com.msp.inventory_manufacturing_service.repository;

import com.msp.inventory_manufacturing_service.entity.ProductMaster;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ProductMasterRepository extends JpaRepository<ProductMaster, Integer> {
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select p from ProductMaster p where p.productId = :id")
    Optional<ProductMaster> findByIdForUpdate(@Param("id") Integer id);

    @Query("select p from ProductMaster p where p.qtyOnHand <= p.reorderLevel")
    List<ProductMaster> findLowStock();
}
