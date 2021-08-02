package com.wangwren.springcloud.controller;

import com.wangwren.springcloud.service.PaymentService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
public class PaymentController {

    @Autowired
    private PaymentService paymentService;

    @GetMapping("payment/hystrix/ok/{id}")
    public String paymentInfoOk(@PathVariable Integer id) {
        String s = paymentService.paymentInfoOk(id);
        log.info(s);
        return s;
    }


    @GetMapping("payment/hystrix/timeout/{id}")
    public String paymentInfoTimeout(@PathVariable Integer id) {
        String s = paymentService.paymentInfoTimeout(id);
        log.info(s);
        return s;
    }
}
