package com.hoquangnam45.pharmacy.controller;

import com.hoquangnam45.pharmacy.pojo.MomoPaymentNotificationResponse;
import com.hoquangnam45.pharmacy.service.impl.MomoPaymentService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("payment/momo")
public class MomoCallbackController {
    private final MomoPaymentService paymentService;

    public MomoCallbackController(MomoPaymentService paymentService) {
        this.paymentService = paymentService;
    }

    // TODO: Mark transaction as successful or fail then redirect user back to the correspond payment result page of the app
    @PostMapping("notification")
    public void receivePaymentNotification(MomoPaymentNotificationResponse notification) {

    }
}
