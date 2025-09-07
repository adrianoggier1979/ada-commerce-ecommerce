package com.adatech.ecommerce.domain.product;

import com.adatech.ecommerce.domain.base.DomainException;
import com.adatech.ecommerce.domain.base.Identidicable;

import java.math.BigDecimal;
import java.util.Objects;
import java.util.UUID;

public class Product implements Identidicable<UUID> {
    private final UUID id;
    private String name;
    private BigDecimal basePrice;

    public Product(UUID id, String name, BigDecimal basePrice) {
        if (id == null) throw new DomainException("ID producto requerido");
        setName(name);
        setBasePrice(basePrice);
        this.id = id;
    }

    @Override public UUID getId() { return id; }
    public String getName() { return name; }
    public BigDecimal getBasePrice() { return basePrice; }

    public void setName(String name) {
        if (name == null || name.isBlank()) throw new DomainException("Nombre producto requerido");
        this.name = name.trim();
    }
    public void setBasePrice(BigDecimal basePrice) {
        if (basePrice == null || basePrice.signum() < 0) throw new DomainException("Precio inválido");
        this.basePrice = basePrice;
    }

    @Override public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Product)) return false;
        Product p = (Product) o;
        return Objects.equals(id, p.id);
    }
    @Override public int hashCode() { return Objects.hash(id); }
}


