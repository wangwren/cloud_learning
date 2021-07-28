package com.wangwren.springcloud.service;

import com.wangwren.springcloud.entities.CommonResult;
import com.wangwren.springcloud.entities.Payment;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * @FeignClient 注解中的value必须写。
 *      其调用流程是根据value中的值去注册中心找到对应的服务，之后根据接口方法上的GetMapping的地址找到对应的具体接口。
 */
@FeignClient(value = "cloud-payment-service")
public interface PaymentFeignService {

    /**
     * PathVariable 里必须要写id，否则启动会报错
     * @param id
     * @return
     */
    @GetMapping("payment/get/{id}")
    CommonResult<Payment> getPaymentById(@PathVariable("id") Long id);


    @GetMapping("payment/feign/timeout")
    String feignTimeOut();
}
