package com.adatech.ecommerce.domain.base;

import java.util.List;
import java.util.Optional;

public interface Repository <T extends Identidicable <ID>, ID>{
    T save(T entity);
    Optional<T> findById(ID id);
    List<T> findAll();
    T update(T entity);
}
