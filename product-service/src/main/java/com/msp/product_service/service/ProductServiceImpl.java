package com.msp.product_service.service;

import com.msp.product_service.entity.Product;
import com.msp.product_service.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {
    private final ProductRepository repo;

    @Override public Product create(Product p){ return repo.save(p); }
    @Override public Product update(Product p){ return repo.save(p); }
    @Override public Product findById(Integer id){ return repo.findById(id).orElse(null); }
    @Override public List<Product> list(int page, int size){ return repo.findAll(); }
    @Override public int pendingCount(){
        // simple placeholder: products missing unit or inactive
        return (int) repo.findAll().stream().filter(pr -> pr.getUnitID() == null || !Boolean.TRUE.equals(pr.getIsActive())).count();
    }
}

