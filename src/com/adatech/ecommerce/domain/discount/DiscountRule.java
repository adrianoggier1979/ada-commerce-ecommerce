package com.adatech.ecommerce.domain.discount;

import com.adatech.ecommerce.domain.base.DomainException;
import com.adatech.ecommerce.domain.base.Identidicable;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class DiscountRule implements Identidicable<UUID> {
    private UUID id;
    private String name;
    private String description;
    private RuleType ruleType;
    private BigDecimal discountValue;
    private DiscountType discountType;
    private BigDecimal minimumOrderValue;
    private LocalDateTime validFrom;
    private LocalDateTime validUntil;
    private boolean isActive;
    private List<DiscountRule> childRules; // Para reglas compuestas
    private RuleOperator operator; // Para combinar reglas compuestas

    public DiscountRule(UUID id, String name, String description, RuleType ruleType,
                       BigDecimal discountValue, DiscountType discountType,
                       BigDecimal minimumOrderValue, LocalDateTime validFrom, LocalDateTime validUntil) {
        if (id == null) throw new DomainException("ID requerido");
        if (name == null || name.trim().isEmpty()) throw new DomainException("Nombre requerido");
        if (discountValue == null || discountValue.compareTo(BigDecimal.ZERO) <= 0) 
            throw new DomainException("Valor de descuento debe ser mayor que cero");
        if (validFrom == null) throw new DomainException("Fecha de inicio requerida");
        if (validUntil == null) throw new DomainException("Fecha de expiración requerida");
        if (validFrom.isAfter(validUntil)) throw new DomainException("Fecha de inicio debe ser anterior a la de expiración");
        if (minimumOrderValue == null) minimumOrderValue = BigDecimal.ZERO;

        this.id = id;
        this.name = name.trim();
        this.description = description;
        this.ruleType = ruleType;
        this.discountValue = discountValue;
        this.discountType = discountType;
        this.minimumOrderValue = minimumOrderValue;
        this.validFrom = validFrom;
        this.validUntil = validUntil;
        this.isActive = true;
        this.childRules = new ArrayList<>();
        this.operator = RuleOperator.AND; // Por defecto
    }

    @Override
    public UUID getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public RuleType getRuleType() {
        return ruleType;
    }

    public BigDecimal getDiscountValue() {
        return discountValue;
    }

    public DiscountType getDiscountType() {
        return discountType;
    }

    public BigDecimal getMinimumOrderValue() {
        return minimumOrderValue;
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

    public List<DiscountRule> getChildRules() {
        return new ArrayList<>(childRules);
    }

    public RuleOperator getOperator() {
        return operator;
    }

    public boolean isValid() {
        LocalDateTime now = LocalDateTime.now();
        return isActive && 
               now.isAfter(validFrom) && 
               now.isBefore(validUntil);
    }

    public boolean canBeApplied(BigDecimal orderValue) {
        if (!isValid()) return false;
        
        if (ruleType == RuleType.SIMPLE) {
            return orderValue.compareTo(minimumOrderValue) >= 0;
        } else {
            // Para reglas compuestas, verificar que todas las reglas hijas sean aplicables
            return childRules.stream().allMatch(rule -> rule.canBeApplied(orderValue));
        }
    }

    public BigDecimal calculateDiscount(BigDecimal orderValue) {
        if (!canBeApplied(orderValue)) {
            return BigDecimal.ZERO;
        }

        if (ruleType == RuleType.SIMPLE) {
            return calculateSimpleDiscount(orderValue);
        } else {
            return calculateCompoundDiscount(orderValue);
        }
    }

    private BigDecimal calculateSimpleDiscount(BigDecimal orderValue) {
        if (discountType == DiscountType.PERCENTAGE) {
            return orderValue.multiply(discountValue).divide(new BigDecimal("100"));
        } else {
            return discountValue.min(orderValue);
        }
    }

    private BigDecimal calculateCompoundDiscount(BigDecimal orderValue) {
        if (childRules.isEmpty()) {
            return BigDecimal.ZERO;
        }

        if (operator == RuleOperator.AND) {
            // Aplicar todas las reglas y sumar los descuentos
            return childRules.stream()
                    .mapToDouble(rule -> rule.calculateDiscount(orderValue).doubleValue())
                    .sum() > 0 ? childRules.stream()
                    .map(rule -> rule.calculateDiscount(orderValue))
                    .reduce(BigDecimal.ZERO, BigDecimal::add) : BigDecimal.ZERO;
        } else {
            // Aplicar la regla con mayor descuento
            return childRules.stream()
                    .map(rule -> rule.calculateDiscount(orderValue))
                    .max(BigDecimal::compareTo)
                    .orElse(BigDecimal.ZERO);
        }
    }

    public void addChildRule(DiscountRule childRule) {
        if (ruleType != RuleType.COMPOUND) {
            throw new DomainException("Solo reglas compuestas pueden tener reglas hijas");
        }
        childRules.add(childRule);
    }

    public void setOperator(RuleOperator operator) {
        if (ruleType != RuleType.COMPOUND) {
            throw new DomainException("Solo reglas compuestas pueden tener operador");
        }
        this.operator = operator;
    }

    public void deactivate() {
        this.isActive = false;
    }

    @Override
    public String toString() {
        return "DiscountRule{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", ruleType=" + ruleType +
                ", discountValue=" + discountValue +
                ", discountType=" + discountType +
                ", minimumOrderValue=" + minimumOrderValue +
                ", validFrom=" + validFrom +
                ", validUntil=" + validUntil +
                ", isActive=" + isActive +
                ", childRulesCount=" + childRules.size() +
                ", operator=" + operator +
                '}';
    }
}

