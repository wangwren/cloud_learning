package com.wangwren.springcloud.service;

import com.wangwren.springcloud.entities.Payment;

public interface PaymentService {

    int create(Payment payment);

    Payment getPaymentById(Long id);
}
