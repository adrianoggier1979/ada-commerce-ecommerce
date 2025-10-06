package com.adatech.ecommerce.application;

import com.adatech.ecommerce.domain.base.DomainException;
import com.adatech.ecommerce.domain.base.Repository;
import com.adatech.ecommerce.domain.coupon.Coupon;
import com.adatech.ecommerce.domain.coupon.DiscountType;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

public class CouponService {
    private Repository<Coupon, UUID> couponRepo;

    public CouponService(Repository<Coupon, UUID> couponRepo) {
        this.couponRepo = couponRepo;
    }

    public Coupon createCoupon(String code, String description, BigDecimal discountValue,
                              DiscountType discountType, LocalDateTime validFrom, LocalDateTime validUntil,
                              int maxUses, BigDecimal minimumOrderValue) {
        // Verificar si ya existe un cupón con el mismo código
        Optional<Coupon> existingCoupon = couponRepo.findAll().stream()
                .filter(c -> c.getCode().equalsIgnoreCase(code.trim()))
                .findFirst();
        
        if (existingCoupon.isPresent()) {
            throw new DomainException("Ya existe un cupón con el código: " + code);
        }

        Coupon coupon = new Coupon(
                UUID.randomUUID(),
                code,
                description,
                discountValue,
                discountType,
                validFrom,
                validUntil,
                maxUses,
                minimumOrderValue
        );

        return couponRepo.save(coupon);
    }

    public List<Coupon> listAllCoupons() {
        return couponRepo.findAll();
    }

    public List<Coupon> listAvailableCoupons() {
        return couponRepo.findAll().stream()
                .filter(Coupon::isValid)
                .collect(Collectors.toList());
    }

    public Coupon findCouponByCode(String code) {
        return couponRepo.findAll().stream()
                .filter(c -> c.getCode().equalsIgnoreCase(code.trim()))
                .findFirst()
                .orElseThrow(() -> new DomainException("Cupón no encontrado: " + code));
    }

    public Coupon updateCoupon(UUID couponId, String description, BigDecimal discountValue,
                              DiscountType discountType, LocalDateTime validFrom, LocalDateTime validUntil,
                              int maxUses, BigDecimal minimumOrderValue) {
        Coupon coupon = couponRepo.findById(couponId)
                .orElseThrow(() -> new DomainException("Cupón no encontrado"));

        // Crear nuevo cupón con los datos actualizados
        Coupon updatedCoupon = new Coupon(
                coupon.getId(),
                coupon.getCode(), // Mantener el código original
                description,
                discountValue,
                discountType,
                validFrom,
                validUntil,
                maxUses,
                minimumOrderValue
        );

        // Mantener el estado de uso actual
        if (coupon.getCurrentUses() > 0) {
            for (int i = 0; i < coupon.getCurrentUses(); i++) {
                updatedCoupon.use();
            }
        }

        return couponRepo.update(updatedCoupon);
    }

    public void deactivateCoupon(UUID couponId) {
        Coupon coupon = couponRepo.findById(couponId)
                .orElseThrow(() -> new DomainException("Cupón no encontrado"));
        coupon.deactivate();
        couponRepo.update(coupon);
    }

    public void expireCoupon(UUID couponId) {
        Coupon coupon = couponRepo.findById(couponId)
                .orElseThrow(() -> new DomainException("Cupón no encontrado"));
        coupon.expire();
        couponRepo.update(coupon);
    }

    public void expireExpiredCoupons() {
        LocalDateTime now = LocalDateTime.now();
        couponRepo.findAll().stream()
                .filter(coupon -> coupon.getValidUntil().isBefore(now) && coupon.isActive())
                .forEach(coupon -> {
                    coupon.expire();
                    couponRepo.update(coupon);
                });
    }

    public BigDecimal applyCoupon(String code, BigDecimal orderValue, int totalQuantity) {
        Coupon coupon = findCouponByCode(code);
        
        if (!coupon.canBeUsed(orderValue)) {
            throw new DomainException("Cupón no puede ser aplicado. Verifique la validez, fechas y valor mínimo del pedido.");
        }

        BigDecimal discount = coupon.calculateDiscount(orderValue, totalQuantity);
        coupon.use();
        couponRepo.update(coupon);
        
        return discount;
    }

    public Repository<Coupon, UUID> getCouponRepo() {
        return couponRepo;
    }
}

