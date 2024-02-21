package com.hoquangnam45.pharmacy.pojo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
public class MomoCaptureWalletRequestUserInfo {
    private final String name;
    private final String phoneNumber;
    private final String email;
}
