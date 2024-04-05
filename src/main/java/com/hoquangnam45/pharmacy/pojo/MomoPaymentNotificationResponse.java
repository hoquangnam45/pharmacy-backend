package com.hoquangnam45.pharmacy.pojo;

import com.hoquangnam45.pharmacy.constant.MomoPaymentOptionType;
import com.hoquangnam45.pharmacy.constant.MomoPaymentType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.UUID;

@AllArgsConstructor
@Getter
@Builder
public class MomoPaymentNotificationResponse {
    private final String partnerCode;
    private final UUID orderId;
    private final String requestId;
    private final Long amount;
    private final String orderInfo;
    private final String partnerUserId;
    private final String orderType;
    private final Long transId;
    private final Integer resultCode;
    private final String message;
    private final MomoPaymentType payType;
    private final Long responseTime;
    private final String extraData;
    private final String signature;
    private final MomoPaymentOptionType paymentOption;
}
