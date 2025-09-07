package com.adatech.ecommerce.domain.base;

public class DomainException extends RuntimeException {

    public DomainException(String message) {
        super(message);
    }
}
