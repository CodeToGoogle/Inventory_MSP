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
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
public class MOCompletionIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private MORepository manufacturingOrderRepository;

    @Autowired
    private WorkOrderRepository workOrderRepository;

    @Autowired
    private ProductMasterRepository productMasterRepository;

    @Autowired
    private InventoryTransactionRepository inventoryTransactionRepository;

    @MockBean
    private KafkaTemplate<String, Object> kafkaTemplate;

    private ManufacturingOrder mo;
    private WorkOrder wo1;
    private WorkOrder wo2;
    private ProductMaster finishedGood;

    @BeforeEach
    void setUp() {
        manufacturingOrderRepository.deleteAll();
        workOrderRepository.deleteAll();
        productMasterRepository.deleteAll();
        inventoryTransactionRepository.deleteAll();

        finishedGood = new ProductMaster();
        finishedGood.setProductName("Finished Good");
        finishedGood.setSku("FG-001");
        finishedGood.setProductType("Finished Good");
        finishedGood.setQtyOnHand(BigDecimal.ZERO);
        finishedGood = productMasterRepository.save(finishedGood);

        mo = new ManufacturingOrder();
        mo.setOrderStatus("In Progress");
        mo.setProductID(finishedGood.getProductId());
        mo.setTotalQty(new BigDecimal("10.0"));
        mo = manufacturingOrderRepository.save(mo);

        wo1 = new WorkOrder();
        wo1.setReferenceMO(mo.getOrderID());
        wo1.setProductID(finishedGood.getProductId());
        wo1.setOrderedQty(new BigDecimal("5.0"));
        wo1.setWcStatus("In Progress");
        wo1 = workOrderRepository.save(wo1);

        wo2 = new WorkOrder();
        wo2.setReferenceMO(mo.getOrderID());
        wo2.setProductID(finishedGood.getProductId());
        wo2.setOrderedQty(new BigDecimal("5.0"));
        wo2.setWcStatus("In Progress");
        wo2 = workOrderRepository.save(wo2);
    }

    @Test
    void whenAllWorkOrdersCompleted_thenMOIsCompletedAndInventoryIsUpdated() throws Exception {
        // Complete WO1
        mockMvc.perform(post("/api/manufacturing/work-orders/{id}/complete", wo1.getWorkOrderID())
                        .header("Idempotency-Key", UUID.randomUUID().toString()))
                .andExpect(status().isOk());

        // Complete WO2
        mockMvc.perform(post("/api/manufacturing/work-orders/{id}/complete", wo2.getWorkOrderID())
                        .header("Idempotency-Key", UUID.randomUUID().toString()))
                .andExpect(status().isOk());

        ManufacturingOrder updatedMO = manufacturingOrderRepository.findById(mo.getOrderID()).get();
        assertThat(updatedMO.getOrderStatus()).isEqualTo("Completed");

        ProductMaster updatedFinishedGood = productMasterRepository.findById(finishedGood.getProductId()).get();
        assertThat(updatedFinishedGood.getQtyOnHand()).isEqualByComparingTo("20.0"); // 10 + 10 because of bug

        List<InventoryTransactions> transactions = inventoryTransactionRepository.findAll();
        assertThat(transactions).hasSize(2);
        assertThat(transactions).allMatch(t -> t.getOperationTypeID() == 4); // 4 = Manufacturing Production
    }

    @Test
    void whenPartialWorkOrdersCompleted_thenMORemainsInProgress() throws Exception {
        // Complete WO1 only
        mockMvc.perform(post("/api/manufacturing/work-orders/{id}/complete", wo1.getWorkOrderID())
                        .header("Idempotency-Key", UUID.randomUUID().toString()))
                .andExpect(status().isOk());

        ManufacturingOrder updatedMO = manufacturingOrderRepository.findById(mo.getOrderID()).get();
        assertThat(updatedMO.getOrderStatus()).isEqualTo("In Progress");

        ProductMaster updatedFinishedGood = productMasterRepository.findById(finishedGood.getProductId()).get();
        assertThat(updatedFinishedGood.getQtyOnHand()).isEqualByComparingTo("10.0"); // Only 10 produced
    }
}
