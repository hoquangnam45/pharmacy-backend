package com.hoquangnam45.pharmacy.service;

import com.hoquangnam45.pharmacy.entity.TransactionInfo;
import com.hoquangnam45.pharmacy.exception.PaymentUnsuccessException;

public interface IPaymentService<T> {
    T startPayment(TransactionInfo transactionInfo) throws PaymentUnsuccessException;
    void cancelPayment(TransactionInfo transactionInfo);
    void transactPayment(TransactionInfo transactionInfo);
}
