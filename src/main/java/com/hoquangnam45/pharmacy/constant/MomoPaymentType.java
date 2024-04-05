package com.hoquangnam45.pharmacy.constant;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.stream.Stream;

@AllArgsConstructor
@Getter
public enum MomoPaymentType {
    WEB_APP("webApp"),
    APP("app"),
    QR("qr"),
    MINI_APP("miniapp"),
    AIO_QR("aio_qr"),
    BANKTRANSFER_QR("banktransfer_qr"),;

    private final String value;

    public static MomoPaymentType parseValue(String value) {
        return Stream.of(values())
                .filter(v -> v.getValue().equals(value))
                .findFirst()
                .orElse(null);
    }
}
