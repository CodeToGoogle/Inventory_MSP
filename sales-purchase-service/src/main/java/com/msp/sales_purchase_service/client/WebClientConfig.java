package com.msp.sales_purchase_service.client;


import org.springframework.context.annotation.*;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfig {
    @Bean public WebClient webClient() { return WebClient.builder().build(); }
}

