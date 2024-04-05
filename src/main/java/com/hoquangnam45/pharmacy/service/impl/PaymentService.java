package com.hoquangnam45.pharmacy.service.impl;

import com.hoquangnam45.pharmacy.constant.TransactionStatus;
import com.hoquangnam45.pharmacy.entity.TransactionInfo;
import com.hoquangnam45.pharmacy.exception.ApiError;
import com.hoquangnam45.pharmacy.exception.PaymentUnsuccessException;
import com.hoquangnam45.pharmacy.repo.TransactionInfoRepo;
import com.hoquangnam45.pharmacy.service.IPaymentService;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.Set;

// Order -> Payment -> Transaction (Void, Partial, Full, Refund)
// Refund transaction will cancel a payment, suppose that the payment is incomplete (partially paid or unpaid at all), a refund transaction will refund all of the previous pay transaction
// Void transaction will be used to verify if a payment method is working or not
// Partial transaction could be used for installment payment, multiple partial transactions if reached the payment amount will trigger a full transaction
// Full transaction will trigger the change of payment status
// A new order created, which the user can then choose a payment method, then proceed the payment
// after proceed with payment, the payment result could be received at a later time, during which
// a transaction info is created with the state as PENDING, after the payment result is confirmed
// partially or full, the state is then proceed to PARTIAL or TRANSACTED accordingly, multiple
// partial transactions that add up to the complete amount will trigger a full transaction will
// complete the transaction, at anypoint in this step, a REFUND transaction could be used which will
// revert past transactions. A cancelled transaction is final and cannot be changed anymore
@Service
public class PaymentService implements IPaymentService<Object> {
    private final MomoPaymentService momoPaymentService;
    private final TransactionInfoRepo transactionInfoRepo;
    private final CodPaymentService codPaymentService;


    private static final Set<TransactionStatus> finalStates = Set.of(
            TransactionStatus.TRANSACTED,
            TransactionStatus.CANCELLED
    );

    public PaymentService(MomoPaymentService momoPaymentService, TransactionInfoRepo transactionInfoRepo, CodPaymentService codPaymentService) {
        this.momoPaymentService = momoPaymentService;
        this.transactionInfoRepo = transactionInfoRepo;
        this.codPaymentService = codPaymentService;
    }

    @Override
    public Object startPayment(TransactionInfo transactionInfo) throws PaymentUnsuccessException {
        if (transactionInfo.getStatus() == TransactionStatus.TRANSACTED) {
            throw ApiError.badRequest("Payment has already completed");
        } else if (transactionInfo.getStatus() == TransactionStatus.CANCELLED) {
            throw ApiError.badRequest("Payment has been cancelled");
        } else if (transactionInfo.getStatus() == TransactionStatus.PENDING) {
            throw ApiError.badRequest("Payment is awaiting user to completed");
        }

        Object result = getPaymentService(transactionInfo).startPayment(transactionInfo);
        if (transactionInfo.getStatus() != TransactionStatus.PENDING)
        {
            // Something must be wrong
            throw new PaymentUnsuccessException("Payment has encountered error");
        }

        transactionInfoRepo.save(transactionInfo);
        return result;
    }

    @Override
    public void cancelPayment(TransactionInfo transactionInfo) {
        transactionInfo.setStatus(TransactionStatus.CANCELLED);
        transactionInfoRepo.save(transactionInfo);
    }

    @Override
    public void transactPayment(TransactionInfo transactionInfo) {
        transactionInfo.setStatus(TransactionStatus.TRANSACTED);
        transactionInfoRepo.save(transactionInfo);
    }

    public IPaymentService<?> getPaymentService(TransactionInfo transactionInfo) {
        return switch (transactionInfo.getPayment().getMethod()) {
            case COD -> codPaymentService;
            case CARD, NAPAS -> throw ApiError.unimplemented("Not supported yet");
            case EWALLET -> {
                if (transactionInfo.getPayment().getMethodDetail().equals("MOMO")) {
                    yield momoPaymentService;
                }
                throw ApiError.unimplemented("Not supported yet");
            }
        };
    }
}
