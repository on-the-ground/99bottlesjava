package com.bottles;

public record Stock(int wall, int store) {
    public Stock apply(StockTx tx) {
        return new Stock(wall + tx.wall(), store + tx.store());
    }
}

