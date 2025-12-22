package com.msp.inventory_manufacturing_service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.msp.inventory_manufacturing_service.entity.ProductMaster;
import com.msp.inventory_manufacturing_service.repository.ProductMasterRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
public class InventoryLevelsIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private ProductMasterRepository productMasterRepository;

    private ProductMaster product;

    @BeforeEach
    void setUp() {
        productMasterRepository.deleteAll();

        product = new ProductMaster();
        product.setProductName("Test Product");
        product.setSku("TP-001");
        product.setQtyOnHand(new BigDecimal("100.0"));
        product.setReservedQty(new BigDecimal("20.0"));
        product = productMasterRepository.save(product);
    }

    @Test
    void whenGetInventoryLevels_thenReturnsCorrectQuantities() throws Exception {
        mockMvc.perform(get("/api/inventory/levels").param("productId", product.getProductId().toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.onHandQty").value(100.0))
                .andExpect(jsonPath("$.reservedQty").value(20.0))
                .andExpect(jsonPath("$.availableQty").value(80.0));
    }

    @Test
    void whenStockIsReserved_thenAvailableQtyDecreases() throws Exception {
        product.setReservedQty(new BigDecimal("30.0"));
        productMasterRepository.save(product);

        mockMvc.perform(get("/api/inventory/levels").param("productId", product.getProductId().toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.availableQty").value(70.0));
    }

    @Test
    void whenStockIsProduced_thenOnHandQtyIncreases() throws Exception {
        product.setQtyOnHand(new BigDecimal("120.0"));
        productMasterRepository.save(product);

        mockMvc.perform(get("/api/inventory/levels").param("productId", product.getProductId().toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.onHandQty").value(120.0))
                .andExpect(jsonPath("$.availableQty").value(100.0));
    }
}
