package com.wangwren.springcloud.service;

import cn.hutool.core.util.IdUtil;
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

    //----服务熔断

    /**
     * 解释一下：
     *  报错或超时执行fallback方法
     *  在10秒窗口期内10次请求中有60%以上失败率，就开启熔断，之后的所有请求都直接走fallback方法，
     *  一段时间后(默认5秒)，自己恢复正常请求
     */
    @HystrixCommand(fallbackMethod = "paymentCircuitBreaker_fallback",commandProperties = {
            @HystrixProperty(name = "circuitBreaker.enabled",value = "true"), //是否开启断路器(熔断)
            @HystrixProperty(name = "circuitBreaker.requestVolumeThreshold",value = "10"), //请求次数
            @HystrixProperty(name = "circuitBreaker.sleepWindowInMilliseconds",value = "10000"), //时间窗口期
            @HystrixProperty(name = "circuitBreaker.errorThresholdPercentage",value = "60") //失败率达到多少后开启熔断(跳闸)
    })
    public String paymentCircuitBreaker(Integer id) {
        if (id < 0) {
            throw new RuntimeException("id不能为负数");
        }

        String uuid = IdUtil.simpleUUID();
        return "线程:" + Thread.currentThread().getName() + "uuid=" + uuid;
    }

    public String paymentCircuitBreaker_fallback(Integer id) {
        return "id 不能为负数，请稍后再试...id" + id;
    }
}
