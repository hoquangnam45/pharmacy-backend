package com.hoquangnam45.pharmacy.service;

import com.hoquangnam45.pharmacy.repo.TransactionInfoRepo;
import org.springframework.stereotype.Service;

@Service
public class TransactionService {
    private final TransactionInfoRepo transactionInfoRepo;

    public TransactionService(TransactionInfoRepo transactionInfoRepo) {
        this.transactionInfoRepo = transactionInfoRepo;
    }

//    private boolean createNewTransaction(String listingId, String[] words, char x) {
//
//    }
}
