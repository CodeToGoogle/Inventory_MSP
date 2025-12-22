package com.msp.sales_purchase_service.repository;

import com.msp.sales_purchase_service.entity.Vendor;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VendorRepository extends JpaRepository<Vendor, Integer> {
}
