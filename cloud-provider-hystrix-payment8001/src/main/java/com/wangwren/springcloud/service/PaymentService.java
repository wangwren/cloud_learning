package com.wangwren.springcloud.service;

import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixProperty;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
@Slf4j
public class PaymentService {

    public String paymentInfoOk(Integer id) {

        return "线程:" + Thread.currentThread().getName() + ";paymentInfoOk id:" + id;
    }


    /**
     * fallbackMethod  兜底的方法，超时或出现异常都会执行该方法，注意该方法的定义要与之前的方法签名一直(包括参数)
     * commandProperties  设置属性
     * timeoutInMilliseconds  设置超时时间，单位毫秒
     *
     */
    @HystrixCommand(fallbackMethod = "timeOutHandler",commandProperties = {
            @HystrixProperty(name = "execution.isolation.thread.timeoutInMilliseconds",value = "5000")
    })
    public String paymentInfoTimeout(Integer id) {
        int time = 4;
        //int a = 1/ 0;
        try {
            TimeUnit.SECONDS.sleep(time);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return "线程:" + Thread.currentThread().getName() + ";paymentInfoTimeout id:" + id;
    }

    public String timeOutHandler(Integer id) {

        return Thread.currentThread().getName() + "系统超时，请稍后再试" + id;
    }
}
