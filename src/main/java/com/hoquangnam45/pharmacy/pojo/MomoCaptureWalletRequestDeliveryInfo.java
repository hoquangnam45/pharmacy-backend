package com.hoquangnam45.pharmacy.pojo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
public class MomoCaptureWalletRequestDeliveryInfo {
    private final String deliveryAddress;
    private final Long deliveryFee;
    private final String quantity;
}
