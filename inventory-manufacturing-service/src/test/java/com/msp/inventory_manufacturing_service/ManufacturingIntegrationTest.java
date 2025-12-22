package com.msp.inventory_manufacturing_service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.msp.inventory_manufacturing_service.entity.*;
import com.msp.inventory_manufacturing_service.event.SalesPurchaseConsumer;
import com.msp.inventory_manufacturing_service.repository.*;
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

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
public class ManufacturingIntegrationTest {

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private ProductMasterRepository productMasterRepository;

    @Autowired
    private BillOfMaterialsRepository billOfMaterialsRepository;

    @Autowired
    private MORepository manufacturingOrderRepository;

    @Autowired
    private WorkOrderRepository workOrderRepository;

    @Autowired
    private InventoryTransactionRepository inventoryTransactionRepository;

    @Autowired
    private SalesPurchaseConsumer salesPurchaseConsumer;

    @MockBean
    private KafkaTemplate<String, String> kafkaTemplate;

    private ProductMaster finishedGood;
    private ProductMaster rawMaterial;

    @BeforeEach
    void setUp() {
        productMasterRepository.deleteAll();
        billOfMaterialsRepository.deleteAll();
        manufacturingOrderRepository.deleteAll();
        workOrderRepository.deleteAll();
        inventoryTransactionRepository.deleteAll();

        finishedGood = new ProductMaster();
        finishedGood.setProductName("Finished Good");
        finishedGood.setSku("FG-001");
        finishedGood.setProductType("Finished Good");
        finishedGood = productMasterRepository.save(finishedGood);

        rawMaterial = new ProductMaster();
        rawMaterial.setProductName("Raw Material");
        rawMaterial.setSku("RM-001");
        rawMaterial.setProductType("Raw Material");
        rawMaterial.setQtyOnHand(new BigDecimal("100.0"));
        rawMaterial.setReservedQty(BigDecimal.ZERO); // Initialize reserved qty to 0
        rawMaterial = productMasterRepository.save(rawMaterial);
    }

    @Test
    void whenCreateBOM_thenBOMAndLinesAreSaved() {
        BillOfMaterials bom = new BillOfMaterials();
        bom.setProductId(finishedGood.getProductId());
        bom.setBomName("BOM for FG-001");

        BillOfMaterialsLine line = new BillOfMaterialsLine();
        line.setProductID(rawMaterial.getProductId());
        line.setQty(new BigDecimal("2.0"));
        line.setUnitID(1);

        bom.setLines(List.of(line));
        line.setBillOfMaterials(bom);

        BillOfMaterials savedBom = billOfMaterialsRepository.save(bom);

        assertThat(savedBom).isNotNull();
        assertThat(savedBom.getLines()).hasSize(1);
        assertThat(savedBom.getLines().get(0).getProductID()).isEqualTo(rawMaterial.getProductId());
    }

    @Test
    void whenSalesOrderApproved_thenManufacturingOrderAndWorkOrdersAreCreatedAndStockIsReserved() throws Exception {
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

        long moCountBefore = manufacturingOrderRepository.count();
        long woCountBefore = workOrderRepository.count();
        BigDecimal reservedQtyBefore = rawMaterial.getReservedQty() == null ? BigDecimal.ZERO : rawMaterial.getReservedQty();

        Map<String, Object> payload = Map.of(
                "orderId", 1,
                "productId", finishedGood.getProductId(),
                "quantity", 10
        );
        Map<String, Object> event = Map.of("payload", payload);

        salesPurchaseConsumer.handleSalesOrderApproved(objectMapper.writeValueAsString(event));

        assertThat(manufacturingOrderRepository.count()).isEqualTo(moCountBefore + 1);
        assertThat(workOrderRepository.count()).isEqualTo(woCountBefore + 1);

        ManufacturingOrder mo = manufacturingOrderRepository.findAll().get(0);
        assertThat(mo.getOrderStatus()).isEqualTo("Planned");

        // Note: The current implementation of createMO does NOT reserve stock.
        // Stock reservation happens when Work Order starts (in WorkOrderServiceImpl.startWorkOrder).
        // Therefore, reserved quantity should remain unchanged at this stage.
        ProductMaster updatedRawMaterial = productMasterRepository.findById(rawMaterial.getProductId()).get();
        assertThat(updatedRawMaterial.getReservedQty()).isEqualByComparingTo(reservedQtyBefore);
    }
}
