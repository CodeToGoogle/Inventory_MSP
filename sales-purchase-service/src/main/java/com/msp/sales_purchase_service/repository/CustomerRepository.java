package com.msp.sales_purchase_service.repository;

import com.msp.sales_purchase_service.entity.Customer;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CustomerRepository extends JpaRepository<Customer, Integer> {
}
