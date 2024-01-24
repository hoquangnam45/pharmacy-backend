package com.hoquangnam45.pharmacy.pojo;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public class FacebookUserInfo {
    private final String email;
    private final String id;
    private final String name;
}
