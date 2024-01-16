package com.hoquangnam45.pharmacy.util;

import com.hoquangnam45.pharmacy.pojo.FunctionEx;

import java.util.function.Consumer;
import java.util.function.Function;

public class Functions {
    public static <T> Function<T, T> peek(Consumer<T> consumer) {
        return input -> {
            consumer.accept(input);
            return input;
        };
    }

    public static <T, R> Function<T, R> suppressException(FunctionEx<T, R> canExceptionFn) {
        return arg -> {
            try {
                return canExceptionFn.apply(arg);
            } catch (Throwable e) {
                throw new RuntimeException(e);
            }
        };
    }
}
