package com.hoquangnam45.pharmacy.service.impl;

import com.amazonaws.services.apigatewayv2.model.Api;
import com.hoquangnam45.pharmacy.constant.TransactionStatus;
import com.hoquangnam45.pharmacy.entity.TransactionInfo;
import com.hoquangnam45.pharmacy.exception.ApiError;
import com.hoquangnam45.pharmacy.repo.TransactionInfoRepo;
import com.hoquangnam45.pharmacy.service.IPaymentService;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.function.BiConsumer;

@Service
public class PaymentService implements IPaymentService {
    private final MomoPaymentService momoPaymentService;
    private final NapasPaymentService napasPaymentService;
    private final TransactionInfoRepo transactionInfoRepo;

    private static final Set<TransactionStatus> finalStates = Set.of(
            TransactionStatus.TRANSACTED,
            TransactionStatus.CANCELLED
    );

    public PaymentService(MomoPaymentService momoPaymentService, NapasPaymentService napasPaymentService, TransactionInfoRepo transactionInfoRepo) {
        this.momoPaymentService = momoPaymentService;
        this.napasPaymentService = napasPaymentService;
        this.transactionInfoRepo = transactionInfoRepo;
    }

    @Override
    public void startPayment(TransactionInfo transactionInfo) {
        if (transactionInfo.getStatus() == TransactionStatus.TRANSACTED) {
            throw ApiError.badRequest("Payment has already completed");
        } else if (transactionInfo.getStatus() == TransactionStatus.CANCELLED) {
            throw ApiError.badRequest("Payment has been cancelled");
        } else if (transactionInfo.getStatus() == TransactionStatus.PENDING) {
            throw ApiError.badRequest("Payment is awaiting user to completed");
        }

        executeTransactionHandler(transactionInfo, IPaymentService::startPayment);
    }

    @Override
    public void cancelPayment(TransactionInfo transactionInfo) {
        if (transactionInfo.getStatus() != TransactionStatus.PENDING) {
            throw ApiError.internalServerError("Payment has not been started yet");
        }
        executeTransactionHandler(transactionInfo, IPaymentService::cancelPayment);
    }

    @Override
    public void transactPayment(TransactionInfo transactionInfo) {
        if (transactionInfo.getStatus() != TransactionStatus.PENDING) {
            throw ApiError.internalServerError("Payment has not been started yet");
        }
        executeTransactionHandler(transactionInfo, IPaymentService::transactPayment);
    }

    public void executeTransactionHandler(TransactionInfo transactionInfo, BiConsumer<IPaymentService, TransactionInfo> transactionHandler) {
        switch (transactionInfo.getPayment().getMethod()) {
            case COD -> transactionInfo.setStatus(TransactionStatus.PENDING);
            case CARD ->
                // TODO: Consider support this type of payment method
                    throw ApiError.unimplemented("Not supported yet");
            case NAPAS -> transactionHandler.accept(napasPaymentService, transactionInfo);
            case EWALLET -> {
                // TODO: Support more type of e-wallet
                if (transactionInfo.getPayment().getMethodDetail().equals("MOMO")) {
                    transactionHandler.accept(momoPaymentService, transactionInfo);
                } else {
                    throw ApiError.unimplemented("Not supported yet");
                }
            }
        }
    }
}
