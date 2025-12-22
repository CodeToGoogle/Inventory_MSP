package com.msp.sales_purchase_service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class SalesPurchaseServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(SalesPurchaseServiceApplication.class, args);
    }

}
