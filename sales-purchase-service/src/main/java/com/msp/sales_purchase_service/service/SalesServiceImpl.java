package com.msp.sales_purchase_service.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.msp.sales_purchase_service.dto.CreateSalesOrderRequest;
import com.msp.sales_purchase_service.dto.SalesOrderResponse;
import com.msp.sales_purchase_service.entity.SalesOrder;
import com.msp.sales_purchase_service.entity.SalesOrderLine;
import com.msp.sales_purchase_service.repository.SalesOrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SalesServiceImpl implements SalesService {

    private final SalesOrderRepository salesOrderRepository;
    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final ObjectMapper objectMapper;

    @Override
    public SalesOrderResponse createSalesOrder(CreateSalesOrderRequest request, String idempotencyKey) {
        SalesOrder salesOrder = new SalesOrder();
        salesOrder.setCustomerId(request.getCustomerId());
        salesOrder.setOrderDate(LocalDate.now());
        salesOrder.setOrderStatus("Draft");

        List<SalesOrderLine> lines = new ArrayList<>();
        BigDecimal totalAmount = BigDecimal.ZERO;
        for (CreateSalesOrderRequest.SalesOrderLineRequest lineRequest : request.getLines()) {
            SalesOrderLine line = new SalesOrderLine();
            line.setOrder(salesOrder);
            line.setProductId(lineRequest.getProductId());
            line.setOrderedQty(lineRequest.getOrderedQty());
            line.setUnitId(lineRequest.getUnitId());
            line.setSaleUnitPrice(lineRequest.getSaleUnitPrice());
            lines.add(line);
            totalAmount = totalAmount.add(line.getSaleUnitPrice().multiply(line.getOrderedQty()));
        }
        salesOrder.setLines(lines);
        salesOrder.setTotalAmount(totalAmount);

        SalesOrder savedOrder = salesOrderRepository.save(salesOrder);
        return toSalesOrderResponse(savedOrder);
    }

    @Override
    public SalesOrderResponse approveSalesOrder(Integer orderId) {
        SalesOrder salesOrder = salesOrderRepository.findById(orderId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "SalesOrder not found with id: " + orderId));

        if (!"Draft".equals(salesOrder.getOrderStatus())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Order is not in Draft state");
        }

        salesOrder.setOrderStatus("Approved");
        SalesOrder updatedOrder = salesOrderRepository.save(salesOrder);
        kafkaTemplate.send("sales.approved", updatedOrder);
        return toSalesOrderResponse(updatedOrder);
    }

    private SalesOrderResponse toSalesOrderResponse(SalesOrder salesOrder) {
        SalesOrderResponse response = new SalesOrderResponse();
        response.setOrderId(salesOrder.getOrderId());
        response.setOrderNumber(salesOrder.getOrderNumber());
        response.setOrderDate(salesOrder.getOrderDate());
        response.setCustomerId(salesOrder.getCustomerId());
        response.setOrderStatus(salesOrder.getOrderStatus());
        response.setTotalAmount(salesOrder.getTotalAmount());
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
