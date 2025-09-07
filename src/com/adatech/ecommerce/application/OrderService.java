package com.adatech.ecommerce.application;

import com.adatech.ecommerce.domain.base.DomainException;
import com.adatech.ecommerce.domain.base.Repository;
import com.adatech.ecommerce.domain.customer.Customer;
import com.adatech.ecommerce.domain.notification.Notifier;
import com.adatech.ecommerce.domain.order.Order;
import com.adatech.ecommerce.domain.order.OrderItem;
import com.adatech.ecommerce.domain.product.Product;

import java.math.BigDecimal;
import java.util.UUID;

public class OrderService {
    private Repository<Order, UUID> orderRepo;
    private Repository<Product, UUID> productRepo;
    private Repository<Customer, UUID> customerRepo;
    private Notifier notifier;

    public OrderService(Repository<Order, UUID> orderRepo,
                        Repository<Product, UUID> productRepo,
                        Repository<Customer, UUID> customerRepo,
                        Notifier notifier) {
        this.orderRepo = orderRepo;
        this.productRepo = productRepo;
        this.customerRepo = customerRepo;
        this.notifier = notifier;
    }

    public Order createOrder(UUID customerId){
    Customer c = customerRepo.findById(customerId).orElseThrow(() -> new DomainException("Cliente nao encontrado"));
    Order o = new Order(UUID.randomUUID(), c.getId());

        o = orderRepo.save(o);
        System.out.println("Data de criacao: " + o.getCreatedAt());
        return o;

    }

    public Order addItem(UUID orderId, UUID productId, int qty, BigDecimal salePrice){
        Order order = getOrder(orderId);
        Product product = productRepo.findById(productId).orElseThrow(() ->  new DomainException("Preducto nao econtrado"));
        order.addItem(new OrderItem(product.getId(), product.getName(), qty, salePrice));
        return orderRepo.update(order);

    }

    public Order removeItem(UUID orderId, UUID productId) {
        Order order = getOrder(orderId); order.removeItem(productId); return orderRepo.update(order);
    }
    public Order changeQuantity(UUID orderId, UUID productId, int qty) {
        Order order = getOrder(orderId); order.changeQuantity(productId, qty);
        return orderRepo.update(order);
    }

    public Order checkout(UUID orderId) {
        Order order = getOrder(orderId); order.checkout();
        Customer c = customerRepo.findById(order.getCustomerId()).orElseThrow(() -> new DomainException("Cliente del pedido no encontrado"));
        notifier.notify(c.getEmail(), "Pedido aguardando pago", "Pedido " + order.getId() + " total: " + order.getTotal());
        return orderRepo.update(order);
    }

    public Order pay(UUID orderId){
        Order order = getOrder(orderId); order.pay();
        Customer c = customerRepo.findById(order.getCustomerId()).orElseThrow(() -> new DomainException("Cliente del pedido no encontrado"));
        notifier.notify(c.getEmail(), "Pago confirmado", "Pago pedido " + order.getId());
        return orderRepo.update(order);

    }

    public Order deliver(UUID orderId) {
        Order order = getOrder(orderId); order.deliver();
        Customer c = customerRepo.findById(order.getCustomerId()).orElseThrow(() -> new DomainException("Cliente del pedido no encontrado"));
        notifier.notify(c.getEmail(), "Pedido entregado", "Pedido " + order.getId() + " entregado");
        return orderRepo.update(order);
    }

    private Order getOrder(UUID id) { return orderRepo.findById(id).orElseThrow(() -> new DomainException("Pedido no encontrado")); }

    public Repository<Order, UUID> getOrderRepo() {
        return orderRepo;
    }
}

