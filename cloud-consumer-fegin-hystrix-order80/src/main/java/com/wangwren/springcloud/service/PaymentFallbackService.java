package com.wangwren.springcloud.service;

import org.springframework.stereotype.Component;

/**
 * 当payment8001 即生产端宕机时，会访问该实现类，对每个方法做处理
 */
@Component
public class PaymentFallbackService implements PaymentHystrixService {
    @Override
    public String paymentInfoOk(Integer id) {
        return "PaymentFallbackService ----- paymentInfoOk";
    }

    @Override
    public String paymentInfoTimeout(Integer id) {
        return "PaymentFallbackService  ----- paymentInfoTimeout";
    }
}
