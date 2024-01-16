package com.hoquangnam45.pharmacy.pojo;

public interface FunctionEx<T, R> {
    R apply(T arg) throws Throwable;
}
