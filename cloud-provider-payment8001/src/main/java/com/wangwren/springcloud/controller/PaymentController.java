package com.wangwren.springcloud.controller;

import com.wangwren.springcloud.entities.CommonResult;
import com.wangwren.springcloud.entities.Payment;
import com.wangwren.springcloud.service.PaymentService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

@RestController()
@RequestMapping("payment")
@Slf4j
public class PaymentController {

    @Resource
    private PaymentService paymentService;

    @PostMapping("create")
    public CommonResult create(@RequestBody Payment payment) {

        int result = paymentService.create(payment);
        log.info("======插入结果 is {}",result);

        if (result > 0) {
            return new CommonResult(200,"插入数据库成功");
        }

        return new CommonResult(500,"插入数据库失败");
    }


    @GetMapping("get/{id}")
    public CommonResult<Payment> getPaymentById(@PathVariable Long id) {

        Payment payment = paymentService.getPaymentById(id);
        log.info("=====查询结果 is {}",payment);
        if (payment != null) {
            return new CommonResult<>(200,"请求成功",payment);
        }

        return new CommonResult<>(500,"未找到对应数据,查询 id=" + id,null);
    }
}
