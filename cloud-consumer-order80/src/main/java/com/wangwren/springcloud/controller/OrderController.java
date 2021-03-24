package com.wangwren.springcloud.controller;

import com.wangwren.springcloud.entities.CommonResult;
import com.wangwren.springcloud.entities.Payment;
import com.wangwren.springcloud.lb.MyLoadBalancerImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@RestController
@RequestMapping("consumer/order")
@Slf4j
public class OrderController {

    //public static final String PAYMENT_URL = "http://localhost:8001";
    //服务提供者集群
    public static final String PAYMENT_URL = "http://CLOUD-PAYMENT-SERVICE";

    @Autowired
    private RestTemplate restTemplate;

    /**
     * 注入自定义的loadBalance
     */
    @Autowired
    private MyLoadBalancerImpl myLoadBalancer;

    @Autowired
    private DiscoveryClient discoveryClient;

    @PostMapping("create")
    public CommonResult create(@RequestBody Payment payment) {

        return restTemplate.postForObject(PAYMENT_URL + "/payment/create",payment,CommonResult.class);
    }

    @GetMapping("get/{id}")
    public CommonResult<Payment> getPayment(@PathVariable("id") Long id) {

        return restTemplate.getForObject(PAYMENT_URL + "/payment/get/" + id,CommonResult.class);
    }

    @GetMapping("getEntity/{id}")
    public CommonResult<Payment> getPaymentForEntity(@PathVariable("id") Long id) {
        ResponseEntity<CommonResult> entity = restTemplate.getForEntity(PAYMENT_URL + "/payment/get/" + id, CommonResult.class);

        if (entity.getStatusCode().is2xxSuccessful()) {
            return entity.getBody();
        } else {
            return new CommonResult<>(500,"系统异常");
        }
    }

    @GetMapping("discovery")
    public CommonResult discovery() {
        return restTemplate.getForObject(PAYMENT_URL + "/payment/discovery",CommonResult.class);
    }


    @GetMapping("getServerPort")
    public String getServerPort() {
        List<ServiceInstance> instances = discoveryClient.getInstances("CLOUD-PAYMENT-SERVICE");

        ServiceInstance instance = myLoadBalancer.instance(instances);
        String uri = instance.getUri().toString();
        log.info("uri is " + uri);

        return restTemplate.getForObject(uri + "/payment/port",String.class);
    }
}
