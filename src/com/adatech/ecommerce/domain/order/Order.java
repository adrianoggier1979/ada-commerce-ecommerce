package com.adatech.ecommerce.domain.order;

import com.adatech.ecommerce.domain.base.DomainException;
import com.adatech.ecommerce.domain.base.Identidicable;
import com.adatech.ecommerce.domain.customer.Customer;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collector;
import java.util.stream.Collectors;

public class Order implements Identidicable<UUID> {

    private UUID id;
    private UUID customerId;
    private LocalDateTime createdAt;
    private OrderStatus status;
    private PaymentStatus paymentStatus;
    private List<OrderItem> items = new ArrayList<>();

    public Order(UUID id, UUID customerId) {
        if (id == null) throw new DomainException("ID requerido");
        if (customerId == null) throw new DomainException("Cliente requerido");
        this.id = id;
        this.customerId = customerId;
        this.createdAt = LocalDateTime.now();
        this.status = OrderStatus.OUPEN;
        this.paymentStatus = PaymentStatus.NOME;


    }

    @Override
    public UUID getId() {
        return id;
    }

    public UUID getCustomerId() {
        return customerId;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public OrderStatus getStatus() {
        return status;
    }

    public PaymentStatus getPaymentStatus() {
        return paymentStatus;
    }



    private void ensureOpen(){
        if (status != OrderStatus.OUPEN) throw new DomainException("Solo pedidos en abierto podem ser modificados");

    }

    public void addItem(OrderItem item){
        ensureOpen();
        Optional<OrderItem> existing = items.stream()
                .filter(i -> i.getProdutId().equals(item.getProdutId()))
                .findFirst();
        if (existing.isPresent()) {
            OrderItem e = existing.get();
            e.setQuantity(e.getQuantity() + item.getQuantity());
            e.setSalePrice(item.getSalePrice());

        }else {
            items.add(item);
        }

    }

    public void removeItem(UUID produtId){
        ensureOpen();items.removeIf(i -> i.getProdutId().equals(produtId));

    }

    public void changeQuantity(UUID produtId, int newQty){
        ensureOpen();
        OrderItem item = items.stream()
                .filter(i ->i.getProdutId().equals(produtId))
                .findFirst()
                .orElseThrow(() -> new DomainException("Item nao encontrado"));
        item.setQuantity(newQty);
    }

    public BigDecimal getTotal(){
       return items.stream().map(OrderItem::getSobtotal).reduce(BigDecimal.ZERO, BigDecimal::add);

}
    public void checkout(){
        if (items.isEmpty()) throw new DomainException("Pedido debe tener al menos 1 item");
        if (getTotal().signum()<=0) throw new DomainException("Total deve ser > o");
        this.paymentStatus = paymentStatus.AWAITING_PAYMENT;

    }
    public void pay(){
        if (paymentStatus != PaymentStatus.AWAITING_PAYMENT) throw  new DomainException("Pago solo si AWAITING_PAYMENT");
       this.paymentStatus = PaymentStatus.PAID;
       
    }

    public void deliver(){
        if (paymentStatus != PaymentStatus.PAID) throw new DomainException("Entrega solo después de Pago");
        this.status = OrderStatus.FINALIZED;
    }
    @Override
    public String toString(){
        String itemsStr = items.stream()
                .map(i -> i.getProductName() + " x" + i.getQuantity() + "=" + i.getSobtotal())
                .collect(Collectors.joining(" ,"));
        return "Order{id= " + id + ", customerId= " + customerId + ", createdAt=" + createdAt + ", status=" + status + ", paymentStatus=" + paymentStatus  + ", total=" + getTotal() +
                ", items=[" + itemsStr + "]}";
    }

}
