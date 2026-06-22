package com.bottles;

public record StockTx(int wall, int store) {
    public StockTx {
        ConstructorGuard.requireCalledFrom("unload", "order");
    }

    public static StockTx unload(int unload) {
        return new StockTx(-unload, 0);
    }

    public static StockTx order(int order) {
        return new StockTx(order, -order);
    }
}
