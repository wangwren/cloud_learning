package com.wangwren.springcloud;

import com.wangwren.myrule.MySelfRule;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.cloud.netflix.ribbon.RibbonClient;

@SpringBootApplication
//SpringCloud E版本之后，不写该注解也能够注册进Eureka
@EnableEurekaClient
//configuration指定使用自己定义的规则配置类；name指定哪一个服务使用这种规则，这个名称一定要与注册中心的一样
//@RibbonClient(name = "CLOUD-PAYMENT-SERVICE",configuration = MySelfRule.class)
public class OrderMain80 {

    public static void main(String[] args) {
        SpringApplication.run(OrderMain80.class,args);
    }
}
