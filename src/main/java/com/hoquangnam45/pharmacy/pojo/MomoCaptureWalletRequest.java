package com.hoquangnam45.pharmacy.pojo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.List;
import java.util.UUID;

// Reference: https://developers.momo.vn/v3/vi/docs/payment/api/wallet/onetime/
@Getter
@AllArgsConstructor
@Builder
public class MomoCaptureWalletRequest {
    private final String partnerCode;
    private final String subPartnerCode;
    private final String storeName;
    private final String requestId;
    private final Long amount;
    private final UUID orderId;
    private final String orderInfo;
    private final Long orderGroupId;
    private final String redirectUrl;
    private final String ipnUrl;
    private final String requestType = "captureWallet";
    private final String extraData;
    private final List<MomoCaptureWalletRequestItem> items;
    private final MomoCaptureWalletRequestDeliveryInfo deliveryInfo;
    private final MomoCaptureWalletRequestUserInfo userInfo;
    private final String referenceId;
    private final boolean autoCapture = true;
    private final String lang = "vi";
    private final String signature;
}
