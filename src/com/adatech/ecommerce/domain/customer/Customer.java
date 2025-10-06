package com.adatech.ecommerce.domain.customer;

import com.adatech.ecommerce.domain.base.DomainException;
import com.adatech.ecommerce.domain.base.Identidicable;

import java.util.Objects;
import java.util.UUID;

public class Customer implements Identidicable <UUID> {
    private UUID id;
    private String name;
    private String documentId;
    private String email;

    public Customer(UUID id, String name, String documentId, String email) {
        if (id == null) throw new DomainException("ID requerido");
        setName(name);
        setDocumentId(documentId);
        setEmail(email);
        this.id = id;

    }


    @Override public UUID getId() { return id; }
    public String getName() { return name; }
    public String getDocumentId() { return documentId; }
    public String getEmail() { return email; }


    public void setName(String name) {
        if (name == null || name.isBlank()) throw new DomainException("Nombre requerido");
        this.name = name.trim();
    }

    public void setDocumentId(String documentId) {
        if (documentId == null || documentId.isBlank()) throw new DomainException("Docuemnto requerido");
        this.documentId = documentId.trim();
    }

    public void setEmail(String email) {
        if (email == null || !email.contains("@")) throw new DomainException("Email invalido");

        this.email = email.trim();
    }

    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Customer)) return false;
        Customer c = (Customer) o;
        return Objects.equals(id, c.id);
    }

    public int hashCode() {
        return Objects.hash(id);
    }
}
