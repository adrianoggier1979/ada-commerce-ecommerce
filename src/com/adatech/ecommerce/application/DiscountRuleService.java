package com.adatech.ecommerce.application;

import com.adatech.ecommerce.domain.base.DomainException;
import com.adatech.ecommerce.domain.base.Repository;
import com.adatech.ecommerce.domain.discount.DiscountRule;
import com.adatech.ecommerce.domain.discount.DiscountType;
import com.adatech.ecommerce.domain.discount.RuleOperator;
import com.adatech.ecommerce.domain.discount.RuleType;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class DiscountRuleService {
    private Repository<DiscountRule, UUID> ruleRepo;

    public DiscountRuleService(Repository<DiscountRule, UUID> ruleRepo) {
        this.ruleRepo = ruleRepo;
    }

    public DiscountRule createSimpleRule(String name, String description, BigDecimal discountValue,
                                       DiscountType discountType, BigDecimal minimumOrderValue,
                                       LocalDateTime validFrom, LocalDateTime validUntil) {
        DiscountRule rule = new DiscountRule(
                UUID.randomUUID(),
                name,
                description,
                RuleType.SIMPLE,
                discountValue,
                discountType,
                minimumOrderValue,
                validFrom,
                validUntil
        );

        return ruleRepo.save(rule);
    }

    public DiscountRule createCompoundRule(String name, String description, RuleOperator operator,
                                         BigDecimal minimumOrderValue, LocalDateTime validFrom, LocalDateTime validUntil) {
        DiscountRule rule = new DiscountRule(
                UUID.randomUUID(),
                name,
                description,
                RuleType.COMPOUND,
                BigDecimal.ZERO, // Las reglas compuestas no tienen valor directo
                DiscountType.FIXED_AMOUNT, // No se usa en reglas compuestas
                minimumOrderValue,
                validFrom,
                validUntil
        );

        rule.setOperator(operator);
        return ruleRepo.save(rule);
    }

    public List<DiscountRule> listAllRules() {
        return ruleRepo.findAll();
    }

    public List<DiscountRule> listActiveRules() {
        return ruleRepo.findAll().stream()
                .filter(DiscountRule::isValid)
                .collect(Collectors.toList());
    }

    public DiscountRule findRuleById(UUID ruleId) {
        return ruleRepo.findById(ruleId)
                .orElseThrow(() -> new DomainException("Regla de descuento no encontrada"));
    }

    public DiscountRule updateRule(UUID ruleId, String name, String description, BigDecimal discountValue,
                                 DiscountType discountType, BigDecimal minimumOrderValue,
                                 LocalDateTime validFrom, LocalDateTime validUntil) {
        DiscountRule existingRule = findRuleById(ruleId);
        
        if (existingRule.getRuleType() == RuleType.COMPOUND) {
            throw new DomainException("No se pueden actualizar reglas compuestas de esta manera");
        }

        DiscountRule updatedRule = new DiscountRule(
                ruleId,
                name,
                description,
                RuleType.SIMPLE,
                discountValue,
                discountType,
                minimumOrderValue,
                validFrom,
                validUntil
        );

        return ruleRepo.update(updatedRule);
    }

    public void addChildRule(UUID parentRuleId, UUID childRuleId) {
        DiscountRule parentRule = findRuleById(parentRuleId);
        DiscountRule childRule = findRuleById(childRuleId);

        if (parentRule.getRuleType() != RuleType.COMPOUND) {
            throw new DomainException("Solo reglas compuestas pueden tener reglas hijas");
        }

        if (childRule.getRuleType() != RuleType.SIMPLE) {
            throw new DomainException("Solo reglas simples pueden ser reglas hijas");
        }

        parentRule.addChildRule(childRule);
        ruleRepo.update(parentRule);
    }

    public void setRuleOperator(UUID ruleId, RuleOperator operator) {
        DiscountRule rule = findRuleById(ruleId);
        
        if (rule.getRuleType() != RuleType.COMPOUND) {
            throw new DomainException("Solo reglas compuestas pueden tener operador");
        }

        rule.setOperator(operator);
        ruleRepo.update(rule);
    }

    public void deactivateRule(UUID ruleId) {
        DiscountRule rule = findRuleById(ruleId);
        rule.deactivate();
        ruleRepo.update(rule);
    }

    public BigDecimal applyBestRule(BigDecimal orderValue) {
        return listActiveRules().stream()
                .filter(rule -> rule.canBeApplied(orderValue))
                .map(rule -> rule.calculateDiscount(orderValue))
                .max(BigDecimal::compareTo)
                .orElse(BigDecimal.ZERO);
    }

    public List<DiscountRule> getApplicableRules(BigDecimal orderValue) {
        return listActiveRules().stream()
                .filter(rule -> rule.canBeApplied(orderValue))
                .collect(Collectors.toList());
    }

    public Repository<DiscountRule, UUID> getRuleRepo() {
        return ruleRepo;
    }
}

