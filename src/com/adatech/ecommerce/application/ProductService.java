package com.adatech.ecommerce.application;

import com.adatech.ecommerce.domain.base.Repository;
import com.adatech.ecommerce.domain.product.Product;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class ProductService {
    private Repository<Product, UUID> repo;

    public ProductService(Repository<Product, UUID> repo) {
        this.repo = repo;
    }

    public Product registrer(String name, BigDecimal basePrice){
        Product p = new Product(UUID.randomUUID(), name, basePrice);
        return repo.save(p);
    }

    public  List<Product> listAll(){
        return repo.findAll();
    }

    public Product update(UUID id, String name, BigDecimal bacePrice) {
        Product existing = repo.findById(id).orElseThrow(() -> new RuntimeException("Producto no encontrado"));
        existing.setName(name);
        existing.setBasePrice(bacePrice);
        return repo.update(existing);
    }

        public Optional<Product> finById(UUID id){
            return repo.findById(id);
        }

}
