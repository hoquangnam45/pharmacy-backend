package com.hoquangnam45.pharmacy.service.impl;

import com.hoquangnam45.pharmacy.entity.TransactionInfo;
import com.hoquangnam45.pharmacy.exception.ApiError;
import com.hoquangnam45.pharmacy.service.IPaymentService;
import org.springframework.stereotype.Service;

@Service
public class NapasPaymentService implements IPaymentService {
    @Override
    public void startPayment(TransactionInfo transactionInfo) {
        throw ApiError.unimplemented("Not implemented yet");
    }

    @Override
    public void cancelPayment(TransactionInfo transactionInfo) {
        throw ApiError.unimplemented("Not implemented yet");
    }

    @Override
    public void transactPayment(TransactionInfo transactionInfo) {
        throw ApiError.unimplemented("Not implemented yet");
    }
}
