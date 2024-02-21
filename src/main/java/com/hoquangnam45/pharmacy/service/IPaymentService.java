package com.hoquangnam45.pharmacy.service;

import com.hoquangnam45.pharmacy.entity.TransactionInfo;

public interface IPaymentService {
    void startPayment(TransactionInfo transactionInfo);
    void cancelPayment(TransactionInfo transactionInfo);
    void transactPayment(TransactionInfo transactionInfo);
}
