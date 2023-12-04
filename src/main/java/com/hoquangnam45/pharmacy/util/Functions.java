package com.hoquangnam45.pharmacy.util;

import java.util.function.Consumer;
import java.util.function.Function;

public class Functions {
    public static <T> Function<T, T> peek(Consumer<T> consumer) {
        return input -> {
            consumer.accept(input);
            return input;
        };
    }
}
