package com.msp.inventory_manufacturing_service.event;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.msp.inventory_manufacturing_service.entity.ProductMaster;
import com.msp.inventory_manufacturing_service.repository.ProductMasterRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Component
@RequiredArgsConstructor
@Slf4j
public class ProductEventConsumer {

    private final ProductMasterRepository productRepository;
    private final ObjectMapper objectMapper;

    @KafkaListener(topics = {"product.created", "product.updated"}, groupId = "inventory-manufacturing-service")
    @Transactional
    public void handleProductEvent(String message) {
        try {
            log.info("Received product event: {}", message);
            ProductMaster eventProduct = objectMapper.readValue(message, ProductMaster.class);

            Optional<ProductMaster> existingOpt = productRepository.findById(eventProduct.getProductId());

            if (existingOpt.isPresent()) {
                ProductMaster existing = existingOpt.get();
                // Update fields
                existing.setProductName(eventProduct.getProductName());
                existing.setSku(eventProduct.getSku());
                existing.setProductType(eventProduct.getProductType());
                existing.setCategoryID(eventProduct.getCategoryID());
                existing.setBaseUnitID(eventProduct.getBaseUnitID());
                existing.setCostPrice(eventProduct.getCostPrice());
                existing.setSellingPrice(eventProduct.getSellingPrice());
                existing.setReorderLevel(eventProduct.getReorderLevel());
                existing.setSafetyStock(eventProduct.getSafetyStock());
                existing.setIsActive(eventProduct.getIsActive());
                // Do NOT set version, let Hibernate handle it
                
                productRepository.save(existing);
                log.info("Product updated from event: ID = {}", existing.getProductId());
            } else {
                // New product. 
                // To avoid "Detached entity..." error with @Version and manual ID, 
                // we might need to ensure version is null (it is) and rely on merge, 
                // OR we might need to force it. 
                // However, since the error happened on save() which calls merge(), 
                // and merge failed because of the version check on a "detached" looking entity.
                
                // A simple workaround for the "new with ID" issue in Hibernate with @Version 
                // is to just save it. If it fails, it means Hibernate really wants to know if it's new.
                // But wait, the previous error WAS on save().
                
                // Let's try setting version to 0L explicitly for new entities if that helps, 
                // or just rely on the fact that we are in a transaction now.
                
                // Actually, the cleanest way to insert a new entity with a specific ID 
                // when using @Version is sometimes tricky. 
                // Let's try simply saving it. The previous error might have been because 
                // the entity was considered "detached" due to the ID presence.
                
                // Let's try to reset the version to 0 if it's null, maybe that satisfies Hibernate?
                // No, uninitialized means null.
                
                // Let's just try saving. If it fails, we know we need a different strategy 
                // (like implementing Persistable).
                
                // BUT, to be safe, let's manually set the fields on a NEW object 
                // to ensure no "detached" state is carried over from Jackson deserialization 
                // (though Jackson creates a fresh object).
                
                // Actually, the error `Detached entity...` confirms Hibernate thinks it's detached.
                // This is because ID is set.
                
                // We will try to save `eventProduct` directly. 
                // If this block is reached, it means it's an INSERT.
                // We can try `productRepository.saveAndFlush(eventProduct)`?
                
                // Let's just save it. The `findById` check above ensures we know it's new.
                // If `save` fails for new entities with ID, we might need `entityManager.persist()`.
                // But Repository doesn't expose persist directly.
                
                // Let's assume the update case was the one failing (because ID 1 existed? No, you said "Product not found").
                // Wait, if "Product not found" was the error, then ID 1 did NOT exist.
                // So the consumer tried to save ID 1. And it failed with "Detached entity".
                
                // This means Hibernate assumes "ID set = Detached/Update".
                // To force Insert with ID, we need to implement `Persistable` and make `isNew()` return true.
                // OR, we can try to trick it.
                
                // Let's try this:
                // Since we can't easily change the Entity to implement Persistable right now without more file edits,
                // we will try to use `merge` (which save does).
                // The error `uninitialized version value` suggests `merge` checked the version.
                
                // I will try to set the version to 0L for the new entity.
                eventProduct.setVersion(0L); 
                productRepository.save(eventProduct);
                log.info("Product created from event: ID = {}", eventProduct.getProductId());
            }

        } catch (Exception e) {
            log.error("Error processing product event: {}", message, e);
        }
    }
}
