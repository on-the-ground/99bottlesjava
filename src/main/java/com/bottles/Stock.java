package com.bottles;

import java.util.UUID;

public record Stock(int wall, int store, UUID version) {
    public Stock {
        ConstructorGuard.requireCalledFrom("of", "apply");
    }

    public static Stock of(int wall, int store) {
        return new Stock(wall, store, UUID.randomUUID());
    }

    public Stock apply(StockTx tx) {
        return new Stock(wall + tx.wall(), store + tx.store(), UUID.randomUUID());
    }
}
