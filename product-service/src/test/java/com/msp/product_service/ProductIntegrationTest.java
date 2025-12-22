package com.msp.product_service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.msp.product_service.entity.ProductMaster;
import com.msp.product_service.repository.ProductRepository;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
public class ProductIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private ProductRepository productRepository;

    @MockBean
    private KafkaTemplate<String, Object> kafkaTemplate;

    @BeforeEach
    void setUp() {
        productRepository.deleteAll();
    }

    @Test
    void whenCreateProduct_thenProductIsSaved() throws Exception {
        long countBefore = productRepository.count();
        ProductMaster product = new ProductMaster();
        product.setProductName("Test Product");
        product.setSku("TP-" + UUID.randomUUID());
        product.setProductType("Finished Good");
        product.setCostPrice(new BigDecimal("10.00"));
        product.setSellingPrice(new BigDecimal("15.00"));

        mockMvc.perform(post("/api/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(product)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.productId").isNumber())
                .andExpect(jsonPath("$.productName").value("Test Product"));

        assertThat(productRepository.count()).isEqualTo(countBefore + 1);
    }

    @Test
    void whenGetProductById_thenReturnsCorrectProduct() throws Exception {
        ProductMaster product = new ProductMaster();
        product.setProductName("Test Product");
        product.setSku("TP-" + UUID.randomUUID());
        product.setProductType("Finished Good");
        product = productRepository.save(product);

        mockMvc.perform(get("/api/products/{id}", product.getProductId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.productName").value("Test Product"))
                .andExpect(jsonPath("$.sku").value(product.getSku()));
    }

    @Test
    void whenCreateDuplicateProductCode_thenReturnsConflict() throws Exception {
        String sku = "TP-" + UUID.randomUUID();
        ProductMaster product1 = new ProductMaster();
        product1.setProductName("Test Product 1");
        product1.setSku(sku);
        product1.setProductType("Finished Good");
        productRepository.save(product1);

        ProductMaster product2 = new ProductMaster();
        product2.setProductName("Test Product 2");
        product2.setSku(sku);
        product2.setProductType("Finished Good");

        mockMvc.perform(post("/api/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(product2)))
                .andExpect(status().isConflict());
    }

    @Test
    void whenListProducts_thenReturnsPaginatedResults() throws Exception {
        for (int i = 0; i < 15; i++) {
            ProductMaster product = new ProductMaster();
            product.setProductName("Product " + i);
            product.setSku("SKU-" + i);
            product.setProductType("Finished Good");
            productRepository.save(product);
        }

        mockMvc.perform(get("/api/products?page=0&size=10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()").value(10))
                .andExpect(jsonPath("$.totalPages").value(2))
                .andExpect(jsonPath("$.totalElements").value(15));
    }
}
