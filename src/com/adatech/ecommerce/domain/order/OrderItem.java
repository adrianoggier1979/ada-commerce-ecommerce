package com.adatech.ecommerce.domain.order;

import com.adatech.ecommerce.domain.base.DomainException;

import java.math.BigDecimal;
import java.util.UUID;

public class OrderItem {
    private  UUID produtId;
    private  String productName;
    private  int quantity;
    private BigDecimal salePrice;


    public OrderItem(UUID produtId, String productName, int quantity, BigDecimal salePrice) {
        if (produtId == null) throw new DomainException("Producto requerido");
        if (productName == null || productName.isBlank()) throw new DomainException("Nome producto requerido");
        if (salePrice == null || salePrice.signum() <= 0) throw new DomainException("Precio de venta invalido");
        setQuantity(quantity);

        this.produtId = produtId;
        this.productName = productName.trim();
        this.salePrice = salePrice;


    }

    public void setQuantity(int q) {
        if (q <=0) throw new DomainException("Cantidad debe ser mayor que cero");
        this.quantity = q;
    }




    public UUID getProdutId() {
        return produtId;
    }

    public String getProductName() {
        return productName;
    }

    public int getQuantity() {
        return quantity;


    }
    public BigDecimal getSalePrice() {
        return salePrice;
    }

    public BigDecimal setSalePrice(BigDecimal price) {
        if (price == null || salePrice.signum() <= 0) throw new DomainException("Precio invalido");
        this.salePrice = price;
        return this.salePrice;
    }

    public  BigDecimal getSobtotal(){
        return salePrice.multiply(BigDecimal.valueOf(quantity));
    }





}
