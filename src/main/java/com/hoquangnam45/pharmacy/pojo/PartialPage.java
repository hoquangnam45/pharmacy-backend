package com.hoquangnam45.pharmacy.pojo;

import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RequiredArgsConstructor
@Getter
@Builder
public class PartialPage<T> {
    private final boolean more;
    private final List<T> data;
}
