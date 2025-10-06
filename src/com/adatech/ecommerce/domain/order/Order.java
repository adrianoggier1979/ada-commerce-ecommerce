package com.adatech.ecommerce.domain.order;

import com.adatech.ecommerce.domain.base.DomainException;
import com.adatech.ecommerce.domain.base.Identidicable;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

public class Order implements Identidicable<UUID> {

    private UUID id;
    private UUID customerId;
    private LocalDateTime createdAt;
    private OrderStatus status;
    private PaymentStatus paymentStatus;
    private List<OrderItem> items = new ArrayList<>();
    private String appliedCouponCode;
    private BigDecimal couponDiscount = BigDecimal.ZERO;
    private BigDecimal ruleDiscount = BigDecimal.ZERO;

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
                .orElseThrow(() -> new DomainException("Item no encontrado"));
        item.setQuantity(newQty);
    }

    public BigDecimal getSubtotal(){
       return items.stream().map(OrderItem::getSobtotal).reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public int getTotalQuantity(){
        return items.stream().mapToInt(OrderItem::getQuantity).sum();
    }

    public BigDecimal getTotal(){
        BigDecimal subtotal = getSubtotal();
        BigDecimal totalDiscount = couponDiscount.add(ruleDiscount);
        return subtotal.subtract(totalDiscount).max(BigDecimal.ZERO);
    }

    public String getAppliedCouponCode() {
        return appliedCouponCode;
    }

    public BigDecimal getCouponDiscount() {
        return couponDiscount;
    }

    public BigDecimal getRuleDiscount() {
        return ruleDiscount;
    }

    public BigDecimal getTotalDiscount() {
        return couponDiscount.add(ruleDiscount);
    }
    public void applyCouponDiscount(String couponCode, BigDecimal discount) {
        // Permitir aplicar cupones a pedidos abiertos o finalizados que no han sido pagados
        if (status != OrderStatus.OUPEN && status != OrderStatus.FINALIZED) {
            throw new DomainException("Solo pedidos abiertos o finalizados pueden tener cupones aplicados");
        }
        if (status == OrderStatus.FINALIZED && paymentStatus == PaymentStatus.PAID) {
            throw new DomainException("No se pueden aplicar cupones a pedidos ya pagados");
        }
        this.appliedCouponCode = couponCode;
        this.couponDiscount = discount;
    }

    public void applyRuleDiscount(BigDecimal discount) {
        this.ruleDiscount = discount;
    }

    public void removeCouponDiscount() {
        // Permitir remover cupones de pedidos abiertos o finalizados que no han sido pagados
        if (status != OrderStatus.OUPEN && status != OrderStatus.FINALIZED) {
            throw new DomainException("Solo pedidos abiertos o finalizados pueden tener cupones removidos");
        }
        if (status == OrderStatus.FINALIZED && paymentStatus == PaymentStatus.PAID) {
            throw new DomainException("No se pueden remover cupones de pedidos ya pagados");
        }
        this.appliedCouponCode = null;
        this.couponDiscount = BigDecimal.ZERO;
    }

    public void checkout(){
        if (items.isEmpty()) throw new DomainException("Pedido debe tener al menos 1 item");
        if (getTotal().signum()<=0) throw new DomainException("Total deve ser > o");
        this.paymentStatus = PaymentStatus.AWAITING_PAYMENT;

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
        
        StringBuilder sb = new StringBuilder();
        sb.append("Order{id= ").append(id)
          .append(", customerId= ").append(customerId)
          .append(", createdAt=").append(createdAt)
          .append(", status=").append(status)
          .append(", paymentStatus=").append(paymentStatus)
          .append(", subtotal=").append(getSubtotal())
          .append(", total=").append(getTotal());
        
        if (appliedCouponCode != null) {
            sb.append(", coupon=").append(appliedCouponCode)
              .append(" (descuento: ").append(couponDiscount).append(")");
        }
        
        if (ruleDiscount.compareTo(BigDecimal.ZERO) > 0) {
            sb.append(", reglaDescuento=").append(ruleDiscount);
        }
        
        sb.append(", items=[").append(itemsStr).append("]}");
        
        return sb.toString();
    }

}
