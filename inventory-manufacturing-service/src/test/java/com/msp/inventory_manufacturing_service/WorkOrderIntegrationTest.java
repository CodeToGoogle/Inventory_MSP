package com.msp.inventory_manufacturing_service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.msp.inventory_manufacturing_service.entity.*;
import com.msp.inventory_manufacturing_service.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
public class WorkOrderIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private WorkOrderRepository workOrderRepository;

    @Autowired
    private ProductMasterRepository productMasterRepository;

    @Autowired
    private MORepository manufacturingOrderRepository;

    @Autowired
    private InventoryTransactionRepository inventoryTransactionRepository;

    @MockBean
    private KafkaTemplate<String, Object> kafkaTemplate;

    private WorkOrder workOrder;
    private ProductMaster rawMaterial;
    private ManufacturingOrder mo;

    @BeforeEach
    void setUp() {
        workOrderRepository.deleteAll();
        manufacturingOrderRepository.deleteAll();
        productMasterRepository.deleteAll();
        inventoryTransactionRepository.deleteAll();

        rawMaterial = new ProductMaster();
        rawMaterial.setProductName("Raw Material");
        rawMaterial.setSku("RM-001");
        rawMaterial.setProductType("Raw Material");
        rawMaterial.setQtyOnHand(new BigDecimal("100.0"));
        rawMaterial.setReservedQty(new BigDecimal("20.0"));
        rawMaterial = productMasterRepository.saveAndFlush(rawMaterial);

        mo = new ManufacturingOrder();
        mo.setProductID(rawMaterial.getProductId()); // Ensure MO has a valid product ID
        mo.setTotalQty(new BigDecimal("10.0"));
        mo.setUnitID(1);
        mo = manufacturingOrderRepository.save(mo);

        workOrder = new WorkOrder();
        workOrder.setReferenceMO(mo.getOrderID());
        workOrder.setProductID(rawMaterial.getProductId());
        workOrder.setOrderedQty(new BigDecimal("10.0"));
        workOrder.setWcStatus("Pending");
        workOrder = workOrderRepository.saveAndFlush(workOrder);
    }

    @Test
    void whenStartWorkOrder_thenStatusIsInProgress() throws Exception {
        mockMvc.perform(post("/api/manufacturing/work-orders/{id}/start", workOrder.getWorkOrderID())
                        .header("Idempotency-Key", UUID.randomUUID().toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("started"));

        WorkOrder updatedWorkOrder = workOrderRepository.findById(workOrder.getWorkOrderID()).get();
        assertThat(updatedWorkOrder.getWcStatus()).isEqualTo("In Progress");
    }

    @Test
    void whenCompleteWorkOrder_thenInventoryIsConsumedAndReservedQtyIsReduced() throws Exception {
        // First, start the work order
        mockMvc.perform(post("/api/manufacturing/work-orders/{id}/start", workOrder.getWorkOrderID())
                .header("Idempotency-Key", UUID.randomUUID().toString()));

        long initialTransactions = inventoryTransactionRepository.count();

        mockMvc.perform(post("/api/manufacturing/work-orders/{id}/complete", workOrder.getWorkOrderID())
                        .header("Idempotency-Key", UUID.randomUUID().toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("completed"));

        WorkOrder completedWorkOrder = workOrderRepository.findById(workOrder.getWorkOrderID()).get();
        assertThat(completedWorkOrder.getWcStatus()).isEqualTo("Completed");

        assertThat(inventoryTransactionRepository.count()).isEqualTo(initialTransactions + 1); 
        InventoryTransactions transaction = inventoryTransactionRepository.findAll().get((int) initialTransactions);
        assertThat(transaction.getOperationTypeID()).isEqualTo(4); // 4 = Manufacturing Production

        ProductMaster updatedRawMaterial = productMasterRepository.findById(rawMaterial.getProductId()).get();
        assertThat(updatedRawMaterial.getReservedQty()).isEqualByComparingTo("10.0"); // 20 - 10
    }

    @Test
    void whenCompleteWorkOrderWithoutStarting_thenReturnsBadRequest() throws Exception {
        mockMvc.perform(post("/api/manufacturing/work-orders/{id}/complete", workOrder.getWorkOrderID())
                        .header("Idempotency-Key", UUID.randomUUID().toString()))
                .andExpect(status().isBadRequest());
    }
}
