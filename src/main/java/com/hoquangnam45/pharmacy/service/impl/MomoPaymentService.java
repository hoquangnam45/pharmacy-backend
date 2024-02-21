package com.hoquangnam45.pharmacy.service.impl;

import com.hoquangnam45.pharmacy.entity.TransactionInfo;
import com.hoquangnam45.pharmacy.service.IPaymentService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

/**
 * References: <a href="https://developers.momo.vn/v3/vi/docs/payment/api/wallet/onetime">...</a>
 * Momo Payment API support 2 type of payment method (one-time, and link the wallet). For simplicity this service
 * will only support the one-time payment method
 */
// When adding momo payment method, we should save some kind of tokens that will be used with momo api
// after that tokens should be used to submit to
@Service
public class MomoPaymentService implements IPaymentService {
    private final String momoApiUrl;
    private final String momoToken;
    private final RestClient restClient;

    public MomoPaymentService(
            @Value("pharma.payment.momo.apiUrl") String momoApiUrl,
            @Value("pharma.payment.momo.apiToken") String momoToken,
            RestClient restClient) {
        this.momoApiUrl = momoApiUrl;
        this.momoToken = momoToken;
        this.restClient = restClient;
    }


    @Override
    public void startPayment(TransactionInfo transactionInfo) {
        restClient.method(HttpMethod)
    }

    @Override
    public void cancelPayment(TransactionInfo transactionInfo) {

    }

    @Override
    public void transactPayment(TransactionInfo transactionInfo) {

    }

    public String getMomoApiUrl(String path) {
        return momoApiUrl + "/" + path;
    }
}
