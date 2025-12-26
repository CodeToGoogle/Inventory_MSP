package com.msp.sales_purchase_service.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.msp.sales_purchase_service.dto.CreateSalesOrderRequest;
import com.msp.sales_purchase_service.dto.SalesOrderResponse;
import com.msp.sales_purchase_service.entity.SalesOrder;
import com.msp.sales_purchase_service.entity.SalesOrderLine;
import com.msp.sales_purchase_service.repository.SalesOrderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class SalesServiceImpl implements SalesService {

    private final SalesOrderRepository salesOrderRepository;
    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final ObjectMapper objectMapper;

    @Override
    @Transactional
    public SalesOrderResponse createSalesOrder(CreateSalesOrderRequest request, String idempotencyKey) {
        SalesOrder salesOrder = new SalesOrder();
        salesOrder.setCustomerId(request.getCustomerId());
        salesOrder.setOrderDate(LocalDate.now());
        salesOrder.setOrderStatus("Draft");

        List<SalesOrderLine> lines = new ArrayList<>();
        BigDecimal totalAmount = BigDecimal.ZERO;
        BigDecimal totalQty = BigDecimal.ZERO;
        for (CreateSalesOrderRequest.SalesOrderLineRequest lineRequest : request.getLines()) {
            SalesOrderLine line = new SalesOrderLine();
            line.setOrder(salesOrder);
            line.setProductId(lineRequest.getProductId());
            line.setOrderedQty(lineRequest.getOrderedQty());
            line.setUnitId(lineRequest.getUnitId());
            line.setSaleUnitPrice(lineRequest.getSaleUnitPrice());
            lines.add(line);
            totalAmount = totalAmount.add(line.getSaleUnitPrice().multiply(line.getOrderedQty()));
            totalQty = totalQty.add(line.getOrderedQty());
        }
        salesOrder.setLines(lines);
        salesOrder.setTotalAmount(totalAmount);
        salesOrder.setTotalQty(totalQty);

        SalesOrder savedOrder = salesOrderRepository.save(salesOrder);
        return toSalesOrderResponse(savedOrder);
    }

    @Override
    @Transactional
    public SalesOrderResponse approveSalesOrder(Integer orderId) {
        SalesOrder salesOrder = salesOrderRepository.findById(orderId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "SalesOrder not found with id: " + orderId));

        if (!"Draft".equals(salesOrder.getOrderStatus())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Order is not in Draft state");
        }

        salesOrder.setOrderStatus("Confirmed"); // Changed from "Approved" to "Confirmed"
        SalesOrder updatedOrder = salesOrderRepository.save(salesOrder);
        
        updatedOrder.getLines().size(); 

        SalesOrderResponse response = toSalesOrderResponse(updatedOrder);
        
        // The Kafka call is still commented out for isolation, we will re-enable it after this is fixed.
        // kafkaTemplate.send("sales.approved", response);
        
        return response;
    }

    private SalesOrderResponse toSalesOrderResponse(SalesOrder salesOrder) {
        SalesOrderResponse response = new SalesOrderResponse();
        response.setOrderId(salesOrder.getOrderId());
        response.setOrderNumber(salesOrder.getOrderNumber());
        response.setOrderDate(salesOrder.getOrderDate());
        response.setCustomerId(salesOrder.getCustomerId());
        response.setOrderStatus(salesOrder.getOrderStatus());
        response.setTotalAmount(salesOrder.getTotalAmount());
        response.setTotalQty(salesOrder.getTotalQty());
        response.setLines(salesOrder.getLines().stream().map(line -> {
            SalesOrderResponse.SalesOrderLineResponse lineResponse = new SalesOrderResponse.SalesOrderLineResponse();
            lineResponse.setLineId(line.getLineId());
            lineResponse.setProductId(line.getProductId());
            lineResponse.setOrderedQty(line.getOrderedQty());
            lineResponse.setSaleUnitPrice(line.getSaleUnitPrice());
            return lineResponse;
        }).collect(Collectors.toList()));
        return response;
    }
}
