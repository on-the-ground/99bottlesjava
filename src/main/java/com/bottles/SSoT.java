package com.bottles;

public interface SSoT {
    void put(String key, Stock value);
    Stock get(String key);
    boolean compareAndSet(String key, Stock expect, Stock update);
}

