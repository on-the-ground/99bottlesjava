package com.bottles;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicReference;

public class InMemorySSoT implements SSoT {
    private final ConcurrentMap<String, AtomicReference<Stock>> storage = new ConcurrentHashMap<>();

    @Override
    public void put(String key, Stock value) {
        storage.computeIfAbsent(key, k -> new AtomicReference<>()).set(value);
    }

    @Override
    public Stock get(String key) {
        AtomicReference<Stock> ref = storage.get(key);
        return (ref != null) ? ref.get() : null;
    }

    @Override
    public boolean compareAndSet(String key, Stock expect, Stock update) {
        AtomicReference<Stock> ref = storage.get(key);
        if (ref == null) return false;
        Stock current = ref.get();
        if (current == null || !current.equals(expect)) return false;
        return ref.compareAndSet(current, update);
    }
}
