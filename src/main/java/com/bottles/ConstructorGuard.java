package com.bottles;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

final class ConstructorGuard {

    private ConstructorGuard() {}

    static void requireCalledFrom(String... allowedMethodNames) {
        Set<String> allowedSet = Arrays.stream(allowedMethodNames).collect(Collectors.toSet());
        boolean isValidCall = java.util.stream.Stream.of(new Throwable().getStackTrace())
                .anyMatch(element -> allowedSet.contains(element.getMethodName()));
        if (!isValidCall) {
            throw new RuntimeException("Don't use bare constructor! Use static factory methods: [" + String.join(", ", allowedMethodNames) + "]");
        }
    }
}
