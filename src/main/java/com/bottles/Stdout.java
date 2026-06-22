package com.bottles;

public enum Stdout implements Output {
    INSTANCE;
    public void out(String str) {
        System.out.println(str);
    }
}

