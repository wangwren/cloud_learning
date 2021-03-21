package com.wangwren.springcloud.controller;

import com.wangwren.springcloud.entities.CommonResult;
import com.wangwren.springcloud.entities.Payment;
import com.wangwren.springcloud.service.PaymentService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

@RestController()
@RequestMapping("payment")
@Slf4j
public class PaymentController {

    @Resource
    private PaymentService paymentService;

    @Value("${server.port}")
    private String serverPort;

    @Resource
    private DiscoveryClient discoveryClient;

    @PostMapping("create")
    public CommonResult create(@RequestBody Payment payment) {

        int result = paymentService.create(payment);
        log.info("======插入结果 is {},serverport={}",result,serverPort);

        if (result > 0) {
            return new CommonResult(200,"插入数据库成功,serverPort=" + serverPort);
        }

        return new CommonResult(500,"插入数据库失败");
    }


    @GetMapping("get/{id}")
    public CommonResult<Payment> getPaymentById(@PathVariable Long id) {

        Payment payment = paymentService.getPaymentById(id);
        log.info("=====查询结果 is {},serverport={}",payment,serverPort);
        if (payment != null) {
            return new CommonResult<>(200,"请求成功,serverPort=" + serverPort,payment);
        }

        return new CommonResult<>(500,"未找到对应数据,查询 id=" + id,null);
    }

    /**
     * 提供服务发现，让别人知道都有什么服务
     * @return
     */
    @GetMapping("/discovery")
    public CommonResult discovery() {
        //获得服务列表清单，获取到的是Eureka中注册的所有服务
        List<String> services = discoveryClient.getServices();
        for (String element : services) {
            log.info("******element:" + element);
        }

        //根据某一个服务获取到其对应的实例
        List<ServiceInstance> instances = discoveryClient.getInstances("CLOUD-PAYMENT-SERVICE");
        for (ServiceInstance instance : instances) {
            log.info(instance.getServiceId() + "\t" + instance.getHost() + "\t" + instance.getPort()
                    + "\t" + instance.getUri());
        }

        return new CommonResult(200,"success",this.discoveryClient);
    }
}
