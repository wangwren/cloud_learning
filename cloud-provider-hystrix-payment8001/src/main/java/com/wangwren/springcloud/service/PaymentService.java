package com.wangwren.springcloud.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
@Slf4j
public class PaymentService {

    public String paymentInfoOk(Integer id) {

        return "线程:" + Thread.currentThread().getName() + ";paymentInfoOk id:" + id;
    }


    public String paymentInfoTimeout(Integer id) {
        int time = 3;

        try {
            TimeUnit.SECONDS.sleep(time);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return "线程:" + Thread.currentThread().getName() + ";paymentInfoTimeout id:" + id;
    }
}
