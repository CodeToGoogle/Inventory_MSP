package com.msp.inventory_manufacturing_service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.msp.inventory_manufacturing_service.entity.*;
import com.msp.inventory_manufacturing_service.event.SalesPurchaseConsumer;
import com.msp.inventory_manufacturing_service.repository.*;
import com.msp.inventory_manufacturing_service.service.InventoryService;
import com.msp.inventory_manufacturing_service.service.WorkOrderService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@ActiveProfiles("test")
@Transactional
public class EndToEndIntegrationTest {

    @Autowired
    private ProductMasterRepository productMasterRepository;

    @Autowired
    private BillOfMaterialsRepository billOfMaterialsRepository;

    @Autowired
    private MORepository manufacturingOrderRepository;

    @Autowired
    private WorkOrderRepository workOrderRepository;

    @Autowired
    private InventoryService inventoryService;

    @Autowired
    private WorkOrderService workOrderService;

    @Autowired
    private SalesPurchaseConsumer salesPurchaseConsumer;

    @MockBean
    private KafkaTemplate<String, Object> kafkaTemplate;

    @Autowired
    private ObjectMapper objectMapper;

    private ProductMaster finishedGood;
    private ProductMaster rawMaterial;

    @BeforeEach
    void setUp() {
        productMasterRepository.deleteAll();
        billOfMaterialsRepository.deleteAll();
        manufacturingOrderRepository.deleteAll();
        workOrderRepository.deleteAll();

        // 1. Setup Local Data (ProductMaster)
        finishedGood = new ProductMaster();
        finishedGood.setProductName("Test FG");
        finishedGood.setSku("FG-001");
        finishedGood.setProductType("Finished Good");
        finishedGood = productMasterRepository.save(finishedGood);

        rawMaterial = new ProductMaster();
        rawMaterial.setProductName("Test RM");
        rawMaterial.setSku("RM-001");
        rawMaterial.setProductType("Raw Material");
        rawMaterial.setQtyOnHand(new BigDecimal("100.0"));
        rawMaterial = productMasterRepository.save(rawMaterial);

        // 2. Setup BOM
        BillOfMaterials bom = new BillOfMaterials();
        bom.setProductId(finishedGood.getProductId());
        bom.setBomName("BOM for FG-001");
        
        BillOfMaterialsLine line = new BillOfMaterialsLine();
        line.setProductID(rawMaterial.getProductId());
        line.setQty(new BigDecimal("2.0"));
        line.setUnitID(1);
        
        bom.setLines(List.of(line));
        line.setBillOfMaterials(bom);
        billOfMaterialsRepository.save(bom);
    }

    @Test
    void fullFlow_fromSalesOrderEventToInventoryUpdate() throws Exception {
        // 3. Simulate Sales Order Approved Event directly via Consumer
        Map<String, Object> payload = Map.of(
                "orderId", 1,
                "productId", finishedGood.getProductId(),
                "quantity", 10
        );
        Map<String, Object> event = Map.of("payload", payload);

        salesPurchaseConsumer.handleSalesOrderApproved(objectMapper.writeValueAsString(event));

        // 4. Verify MO and WO creation
        List<ManufacturingOrder> mos = manufacturingOrderRepository.findAll();
        assertThat(mos).hasSize(1);
        ManufacturingOrder mo = mos.get(0);
        assertThat(mo.getOrderStatus()).isEqualTo("Planned");

        List<WorkOrder> wos = workOrderRepository.findByReferenceMO(mo.getOrderID());
        assertThat(wos).hasSize(1);
        WorkOrder wo = wos.get(0);

        // 5. Execute Work Orders
        workOrderService.startWorkOrder(wo.getWorkOrderID(), UUID.randomUUID().toString());
        workOrderService.completeWorkOrder(wo.getWorkOrderID(), UUID.randomUUID().toString());

        // 6. Verify final inventory state
        ProductMaster finalFg = productMasterRepository.findById(finishedGood.getProductId()).get();
        ProductMaster finalRm = productMasterRepository.findById(rawMaterial.getProductId()).get();

        // FG should increase by 10 (produced)
        assertThat(finalFg.getQtyOnHand()).isEqualByComparingTo("10.0");
        
        // RM should decrease by 20 (consumed: 10 units * 2 per unit)
        // Initial 100 - 20 consumed = 80
        assertThat(finalRm.getQtyOnHand()).isEqualByComparingTo("80.0");
        
        // Reserved qty should be 0 as it was never reserved
        assertThat(finalRm.getReservedQty()).isEqualByComparingTo("0.0");
    }
}
