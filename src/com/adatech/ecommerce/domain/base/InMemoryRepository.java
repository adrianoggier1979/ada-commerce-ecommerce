package com.adatech.ecommerce.domain.base;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class InMemoryRepository <T extends  Identidicable <ID>, ID> implements Repository <T, ID>{
    private final Map<ID, T> storage = new ConcurrentHashMap<>();

    @Override
    public T save(T entity) {
        if (entity instanceof Identidicable) {
            Identidicable<ID> identifiable = (Identidicable<ID>) entity;
            storage.put(identifiable.getId(), entity);
            return entity;
        }
        throw new IllegalArgumentException("A entidade deve implementar Identificável");
    }


@Override
public Optional<T> findById(ID id) {
    return Optional.ofNullable(storage.get(id));
}



@Override
public List<T> findAll() {
    return new ArrayList<>(storage.values());
}




    @Override
    public T update(T entity) {
        if (entity instanceof Identidicable) {
            Identidicable<ID> identifiable = (Identidicable<ID>) entity;
            if (!storage.containsKey(identifiable.getId())) {
                throw new NoSuchElementException("Entidade não encontrada");
            }
            storage.put(identifiable.getId(), entity);
            return entity;
        }
        throw new IllegalArgumentException(
                "A entidade deve implementar Identificável");
    }


}
