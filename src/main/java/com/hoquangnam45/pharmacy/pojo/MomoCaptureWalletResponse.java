package com.hoquangnam45.pharmacy.pojo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Getter
@AllArgsConstructor
@Builder
public class MomoCaptureWalletResponse {
    private final String partnerCode;
    private final String requestId;
    private final UUID orderId;
    private final Long amount;
    private final Long responseTime;
    private final String message;
    private final Integer resultCode;
    private final String payUrl;
    private final String deeplink;
    private final String qrCodeUrl;
    private final String deeplinkMiniApp;
    private final String signature;
}
