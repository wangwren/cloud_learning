# Consul服务注册与发现
## 简介
Consul是一套开源的分布式服务发现和配置管理系统，由HashiCorp公司用Go语言开发。

提供了微服务系统中的服务治理、配置中心、控制总线等功能。这些功能中的每一个都可以根据需要单独使用，也可以一起使用以构建全方位的服务网格，总之Consul提供了一种完整的服务网格解决方案。

它具有很多优点，包括：基于raft协议，比较简洁；支持健康检查，同时支持HTTP和DNS协议，支持跨数据中心的WAN集群，提供图形界面；跨平台。

[Consul中文文档](https://www.springcloud.cc/spring-cloud-consul.html)

## 提供的功能
- 服务发现
    - 提供HTTP和DNS两种发现方式。
- 健康监测
    - 支持多种方式，HTTP、TCP、Docker、Shell脚本定制化。
- KV存储
    - Key、Value的存储方式。
- 多数据中心
    - Consul支持多数据中心。
- 可视化Web界面

## Mac安装运行consul
[下载地址](https://www.consul.io/downloads)

下载好后，解压安装包，使用命令行进入解压出来的目录(当然也可以选择一个你想要存放的地址)，执行`./consul --version`查看consul版本。

启动consul：`./consul agent -dev`使用开发模式启动，启动后，在浏览器中输入`http://localhost:8500`即可访问consul界面。

停止启动：直接在刚启动的命令行执行Ctrl + c。

## 代码
- pom.xml

```
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-starter-consul-discovery</artifactId>
    <!--这个必须加版本号，不加默认会下载2.2.1，但是根本下载不下来；3的版本启动还会报错，2.2的版本只有2.2.6能用-->
    <version>2.2.6.RELEASE</version>
</dependency>
```

### 服务提供者
- yml配置文件

```yaml
server:
  port: 8006

spring:
  application:
    name: consul-provider-payment
  #consul注册中心地址
  cloud:
    consul:
      host: localhost
      port: 8500
      discovery:
        heartbeat:
          #必须加，心跳检测
          enabled: true
        #去找哪个服务名称
        service-name: ${spring.application.name}
```

- java代码

```java
package com.wangwren.springcloud.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("payment")
@Slf4j
public class PaymentController {

    @Value("${server.port}")
    private String port;

    @RequestMapping("consul")
    public String paymentZk() {

        return "spring cloud with consul:" + port + "\t" + UUID.randomUUID().toString();
    }
}
```

### 服务消费者
- yml配置文件

```yaml
server:
  port: 80

spring:
  application:
    name: cloud-consumer-order
  #consul注册中心地址
  cloud:
    consul:
      host: localhost
      port: 8500
      discovery:
        heartbeat:
          #必须加，心跳检测
          enabled: true
        #去找哪个服务名称
        service-name: ${spring.application.name}
```

- java

```java
package com.wangwren.springcloud.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;

@RestController
@Slf4j
public class OrderConsulController {

    //地址是注册在consul的服务提供者名称
    public static final String INVOKE_URL = "http://consul-provider-payment";

    @Resource
    private RestTemplate restTemplate;

    @GetMapping("consumer/consul/info")
    public String paymentInfo() {

        return restTemplate.getForObject(INVOKE_URL + "/payment/consul",String.class);
    }
}
```

==consul在代码上其实与zookeeper代码差不多，都是注册中心，其思想差不多；zookeeper与consul都是需要安装在服务器上的；而Eureka需要写项目实现，没办法安装==