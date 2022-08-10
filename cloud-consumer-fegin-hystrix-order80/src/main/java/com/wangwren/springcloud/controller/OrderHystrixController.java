package com.wangwren.springcloud.controller;

import com.netflix.hystrix.contrib.javanica.annotation.DefaultProperties;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixProperty;
import com.wangwren.springcloud.service.PaymentHystrixService;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
/**
 * 全局的配置，如果@HystrixCommand配置了fallBack方法，那么就执行对应的方法，
 * 如果@HystrixCommand没有指定，就会执行@DefaultProperties 指定的方法
 *
 * 前提是方法上有@HystrixCommand注解
 */
@DefaultProperties(defaultFallback = "globalTimeOut")
public class OrderHystrixController {

    private static final Logger LOGGER = LogManager.getLogger(OrderHystrixController.class);

    @Autowired
    private PaymentHystrixService paymentHystrixService;

    @GetMapping("consumer/payment/hystrix/ok/{id}")
    public String paymentInfoOk(@PathVariable Integer id){
        return paymentHystrixService.paymentInfoOk(id);
    }


    @GetMapping("consumer/payment/hystrix/timeout/{id}")
//    @HystrixCommand(fallbackMethod = "timeOutHandler",commandProperties = {
//            @HystrixProperty(name = "execution.isolation.thread.timeoutInMilliseconds",value = "1500")
//    })
    @HystrixCommand
    public String paymentInfoTimeout(@PathVariable Integer id) {
        int a = 1 / 0;
        return paymentHystrixService.paymentInfoTimeout(id);
    }

    public String timeOutHandler(Integer id) {
        return "我是消费端80，接口超时";
    }

    public String globalTimeOut() {
        return "全局超时异常执行方法";
    }

    public static void main(String[] args) {
        String str = "try ${date:YY-mm-dd}";
        LOGGER.error(str);
    }
}
