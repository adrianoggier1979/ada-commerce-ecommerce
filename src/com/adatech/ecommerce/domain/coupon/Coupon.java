package com.adatech.ecommerce.domain.coupon;

import com.adatech.ecommerce.domain.base.DomainException;
import com.adatech.ecommerce.domain.base.Identidicable;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public class Coupon implements Identidicable<UUID> {
    private UUID id;
    private String code;
    private String description;
    private BigDecimal discountValue;
    private DiscountType discountType;
    private LocalDateTime validFrom;
    private LocalDateTime validUntil;
    private boolean isActive;
    private boolean isUsed;
    private int maxUses;
    private int currentUses;
    private BigDecimal minimumOrderValue;

    public Coupon(UUID id, String code, String description, BigDecimal discountValue, 
                  DiscountType discountType, LocalDateTime validFrom, LocalDateTime validUntil,
                  int maxUses, BigDecimal minimumOrderValue) {
        if (id == null) throw new DomainException("ID requerido");
        if (code == null || code.trim().isEmpty()) throw new DomainException("Código requerido");
        if (discountValue == null || discountValue.compareTo(BigDecimal.ZERO) <= 0) 
            throw new DomainException("Valor de descuento debe ser mayor que cero");
        if (validFrom == null) throw new DomainException("Fecha de inicio requerida");
        if (validUntil == null) throw new DomainException("Fecha de expiración requerida");
        if (validFrom.isAfter(validUntil)) throw new DomainException("Fecha de inicio debe ser anterior a la de expiración");
        if (maxUses < 0) throw new DomainException("Máximo de usos no puede ser negativo");
        if (minimumOrderValue == null) minimumOrderValue = BigDecimal.ZERO;

        this.id = id;
        this.code = code.trim().toUpperCase();
        this.description = description;
        this.discountValue = discountValue;
        this.discountType = discountType;
        this.validFrom = validFrom;
        this.validUntil = validUntil;
        this.isActive = true;
        this.isUsed = false;
        this.maxUses = maxUses;
        this.currentUses = 0;
        this.minimumOrderValue = minimumOrderValue;
    }

    @Override
    public UUID getId() {
        return id;
    }

    public String getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }

    public BigDecimal getDiscountValue() {
        return discountValue;
    }

    public DiscountType getDiscountType() {
        return discountType;
    }

    public LocalDateTime getValidFrom() {
        return validFrom;
    }

    public LocalDateTime getValidUntil() {
        return validUntil;
    }

    public boolean isActive() {
        return isActive;
    }

    public boolean isUsed() {
        return isUsed;
    }

    public int getMaxUses() {
        return maxUses;
    }

    public int getCurrentUses() {
        return currentUses;
    }

    public BigDecimal getMinimumOrderValue() {
        return minimumOrderValue;
    }

    public boolean isValid() {
        LocalDateTime now = LocalDateTime.now();
        return isActive && 
               !isUsed && 
               now.isAfter(validFrom) && 
               now.isBefore(validUntil) && 
               (maxUses == 0 || currentUses < maxUses);
    }

    public boolean canBeUsed(BigDecimal orderValue) {
        return isValid() && orderValue.compareTo(minimumOrderValue) >= 0;
    }

    public BigDecimal calculateDiscount(BigDecimal orderValue) {
        return calculateDiscount(orderValue, 0);
    }

    public BigDecimal calculateDiscount(BigDecimal orderValue, int totalQuantity) {
        if (!canBeUsed(orderValue)) {
            return BigDecimal.ZERO;
        }

        if (discountType == DiscountType.PERCENTAGE) {
            return orderValue.multiply(discountValue).divide(new BigDecimal("100"));
        } else if (discountType == DiscountType.PROGRESSIVE) {
            return calculateProgressiveDiscount(orderValue, totalQuantity);
        } else {
            return discountValue.min(orderValue);
        }
    }

    private BigDecimal calculateProgressiveDiscount(BigDecimal orderValue, int totalQuantity) {
        BigDecimal discountPercentage;
        
        if (totalQuantity <= 10) {
            discountPercentage = new BigDecimal("10"); // 10%
        } else if (totalQuantity <= 50) {
            discountPercentage = new BigDecimal("15"); // 15%
        } else {
            discountPercentage = new BigDecimal("20"); // 20%
        }
        
        return orderValue.multiply(discountPercentage).divide(new BigDecimal("100"));
    }

    public void use() {
        // No validar aquí porque ya se validó en applyCoupon
        currentUses++;
        if (maxUses > 0 && currentUses >= maxUses) {
            isUsed = true;
        }
    }

    public void deactivate() {
        this.isActive = false;
    }

    public void expire() {
        this.isActive = false;
    }

    @Override
    public String toString() {
        return "Coupon{" +
                "id=" + id +
                ", code='" + code + '\'' +
                ", description='" + description + '\'' +
                ", discountValue=" + discountValue +
                ", discountType=" + discountType +
                ", validFrom=" + validFrom +
                ", validUntil=" + validUntil +
                ", isActive=" + isActive +
                ", isUsed=" + isUsed +
                ", maxUses=" + maxUses +
                ", currentUses=" + currentUses +
                ", minimumOrderValue=" + minimumOrderValue +
                '}';
    }
}

