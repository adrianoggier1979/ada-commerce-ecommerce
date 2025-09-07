package com.adatech.ecommerce.application;

import com.adatech.ecommerce.domain.base.Repository;
import com.adatech.ecommerce.domain.customer.Customer;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class CustomerService {
    private final Repository<Customer, UUID> repo;


    public CustomerService(Repository<Customer, UUID> repo) {
        this.repo = repo;
    }

    public Customer register (String name, String documentId, String email){

        Customer c = new Customer(UUID.randomUUID(), name, documentId, email);
        return repo.save(c);
    }

    public List<Customer> listAll(){
        return repo.findAll();

    }
    public Customer update(UUID id, String name, String documentId, String email){
        Customer existing = repo.findById(id).orElseThrow(() -> new RuntimeException("Cliente no encontrado"));
        existing.setName(name); existing.setDocumentId(documentId); existing.setEmail(email);
        return repo.update(existing);
    }

    public Optional<Customer> finById(UUID id){
        return repo.findById(id);
    }
}
