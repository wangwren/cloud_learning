package com.wangwren.springcloud.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("payment")
@Slf4j
public class PaymentController {


    @Value("${server.port}")
    private String port;

    @RequestMapping("zk")
    public String paymentZk() {

        return "spring cloud with zk:" + port + "\t" + UUID.randomUUID().toString();
    }
}
