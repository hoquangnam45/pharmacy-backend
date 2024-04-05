package com.hoquangnam45.pharmacy.controller;

import com.hoquangnam45.pharmacy.service.impl.PaymentService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController("payment")
public class PaymentController {
    private final PaymentService paymentService;

    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @PostMapping("")
    public void startPayment(PaymentCreateRequest paymentCreateRequest)
    {

    }
}
