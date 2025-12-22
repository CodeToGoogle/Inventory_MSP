package com.msp.sales_purchase_service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.msp.sales_purchase_service.dto.CreateSalesOrderRequest;
import com.msp.sales_purchase_service.entity.Customer;
import com.msp.sales_purchase_service.entity.SalesOrder;
import com.msp.sales_purchase_service.repository.CustomerRepository;
import com.msp.sales_purchase_service.repository.SalesOrderRepository;
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
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
public class SalesOrderIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private SalesOrderRepository salesOrderRepository;

    @Autowired
    private CustomerRepository customerRepository;

    @MockBean
    private KafkaTemplate<String, Object> kafkaTemplate;

    private Customer customer;

    @BeforeEach
    void setUp() {
        salesOrderRepository.deleteAll();
        customerRepository.deleteAll();
        customer = new Customer();
        customer.setCustomerName("Test Customer");
        customer = customerRepository.save(customer);
    }

    @Test
    void whenCreateSalesOrder_thenOrderIsSaved() throws Exception {
        long countBefore = salesOrderRepository.count();
        CreateSalesOrderRequest.SalesOrderLineRequest line = new CreateSalesOrderRequest.SalesOrderLineRequest();
        line.setProductId(1);
        line.setOrderedQty(new BigDecimal("10.0"));
        line.setUnitId(1);
        line.setSaleUnitPrice(new BigDecimal("100.00"));

        CreateSalesOrderRequest request = new CreateSalesOrderRequest();
        request.setCustomerId(customer.getCustomerId());
        request.setLines(List.of(line));

        mockMvc.perform(post("/api/sales/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.orderId").isNumber())
                .andExpect(jsonPath("$.orderStatus").value("Draft"));

        assertThat(salesOrderRepository.count()).isEqualTo(countBefore + 1);
    }

    @Test
    void whenApproveSalesOrder_thenStatusIsApprovedAndEventIsPublished() throws Exception {
        SalesOrder salesOrder = new SalesOrder();
        salesOrder.setCustomerId(customer.getCustomerId());
        salesOrder.setOrderStatus("Draft");
        salesOrder = salesOrderRepository.save(salesOrder);

        mockMvc.perform(post("/api/sales/orders/{id}/approve", salesOrder.getOrderId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.orderStatus").value("Approved"));

        verify(kafkaTemplate, times(1)).send("sales.approved", salesOrder);
    }

    @Test
    void whenCreateOrderWithIdempotencyKey_thenOnlyOneOrderIsCreated() throws Exception {
        String idempotencyKey = UUID.randomUUID().toString();
        long countBefore = salesOrderRepository.count();

        CreateSalesOrderRequest.SalesOrderLineRequest line = new CreateSalesOrderRequest.SalesOrderLineRequest();
        line.setProductId(1);
        line.setOrderedQty(new BigDecimal("10.0"));
        line.setUnitId(1);
        line.setSaleUnitPrice(new BigDecimal("100.00"));

        CreateSalesOrderRequest request = new CreateSalesOrderRequest();
        request.setCustomerId(customer.getCustomerId());
        request.setLines(List.of(line));

        mockMvc.perform(post("/api/sales/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .header("Idempotency-Key", idempotencyKey))
                .andExpect(status().isOk());

        assertThat(salesOrderRepository.count()).isEqualTo(countBefore + 1);

        // Send the same request again with the same idempotency key
        mockMvc.perform(post("/api/sales/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .header("Idempotency-Key", idempotencyKey))
                .andExpect(status().isOk());

        assertThat(salesOrderRepository.count()).isEqualTo(countBefore + 1);
    }

    @Test
    void whenApproveAlreadyApprovedOrder_thenReturnsBadRequest() throws Exception {
        SalesOrder salesOrder = new SalesOrder();
        salesOrder.setCustomerId(customer.getCustomerId());
        salesOrder.setOrderStatus("Approved");
        salesOrder = salesOrderRepository.save(salesOrder);

        mockMvc.perform(post("/api/sales/orders/{id}/approve", salesOrder.getOrderId()))
                .andExpect(status().isBadRequest());
    }
}
