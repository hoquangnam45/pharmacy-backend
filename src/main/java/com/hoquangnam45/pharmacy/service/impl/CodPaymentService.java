package com.hoquangnam45.pharmacy.service.impl;

import com.hoquangnam45.pharmacy.constant.TransactionStatus;
import com.hoquangnam45.pharmacy.entity.TransactionInfo;
import com.hoquangnam45.pharmacy.service.IPaymentService;

public class CodPaymentService implements IPaymentService<TransactionStatus> {
    @Override
    public TransactionStatus startPayment(TransactionInfo transactionInfo) {
        transactionInfo.setStatus(TransactionStatus.PENDING);
        return transactionInfo.getStatus();
    }

    @Override
    public void cancelPayment(TransactionInfo transactionInfo) {

    }

    @Override
    public void transactPayment(TransactionInfo transactionInfo) {

    }
}
