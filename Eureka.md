# Eureka服务注册与发现
## Eureka基础
### 什么是服务治理
SpringCloud封装了Netflix公司开发的Eureka模块来实现服务治理。
在传统的rpc远程调用框架中，管理每个服务与服务之间依赖关系比较复杂，管理比较复杂，所以需要使用服务治理，管理服务与服务之间依赖关系，可以实现服务调用、负载均衡、容错等，实现服务发现与注册。

### 什么是服务注册与发现
Eureka采用了CS的设计架构，Eureka Server作为服务注册功能的服务器，它是服务注册中心。而系统中的其他微服务，使用Eureka的客户端连接到Eureka Server并维持心跳连接。这样系统的维护人员就可以通过Eureka Server来监控系统中各个微服务是否正常运行。

在服务注册与发现中，有一个注册中心。当服务器启动时，会把当前自己服务器的信息，比如服务地址、通讯地址等以别名方式注册到注册中心上。另一方(消费者服务提供者)，以该别名的方式去注册中心获取到实际的服务通讯地址，然后再实现本地RPC调用，RPC远程调用框架核心设计思想：在于注册中心，因为使用注册中心管理每个服务与服务之间的依赖关系(服务治理概念)。在任何RPC框架中，都会有一个注册中心(存放服务地址相关信息(接口地址))。
![](https://imagebed-1259286100.cos.ap-beijing.myqcloud.com/img/2021-03-19_00-22-20.png)

### Eureka的两个组件
- ==EurekaServer==提供服务注册服务：各个微服务节点通过配置启动后，会在EurekaServer中进行注册，这样EurekaServer中的服务注册表中将会存储所有可用服务节点的信息，服务节点的信息可以在界面中直观看到。
- ==EurekaClient==通过注册中心进行访问：是一个Java客户端，用于简化EurekaServer的交互，客户端同时也具备一个内置的、使用轮询(round-robin)负载算法的负载均衡器。在应用启动后，将会向EurekaServer发送心跳(默认周期为30秒)。如果EurekaServer在多个心跳周期内没有接收到某个节点的心跳，EurekaServer将会从服务注册表中把这个服务节点移除(默认90秒)。

### 使用
- 对应项目：*cloud-eureka-server7001*
- 创建一个maven项目，引入pom
    - 注意这里引入的是server端，Eureka的artifactId别写错了，有个starter。

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <parent>
        <artifactId>cloud_learning</artifactId>
        <groupId>com.wangwren.springcloud</groupId>
        <version>1.0-SNAPSHOT</version>
    </parent>
    <modelVersion>4.0.0</modelVersion>
    <artifactId>cloud-eureka-server7001</artifactId>
    
    <dependencies>
        <!--eureka server-->
        <dependency>
            <groupId>org.springframework.cloud</groupId>
            <artifactId>spring-cloud-starter-netflix-eureka-server</artifactId>
        </dependency>
        <!--引入自定定义的api通用包-->
        <dependency>
            <groupId>com.wangwren.springcloud</groupId>
            <artifactId>cloud-api-commons</artifactId>
            <version>1.0-SNAPSHOT</version>
        </dependency>

        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-web</artifactId>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-actuator</artifactId>
        </dependency>
        <!--一般通用配置-->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-devtools</artifactId>
            <scope>runtime</scope>
            <optional>true</optional>
        </dependency>
        <dependency>
            <groupId>org.projectlombok</groupId>
            <artifactId>lombok</artifactId>
            <optional>true</optional>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-test</artifactId>
            <scope>test</scope>
        </dependency>
    </dependencies>
</project>
```

- application.yml

```yml
server:
  port: 7001
eureka:
  instance:
    hostname: localhost #eureka服务端的实例名
  client:
    #false表示不向注册中心注册自己
    register-with-eureka: false
    #false表示自己端就是注册中心，我的职责就是维护服务实例，并不需要去检索服务
    fetch-registry: false
    service-url:
      #设置与EurekaServer交互的地址查询服务和注册服务都需要依赖这个地址，即上面配置的：http://localhost:7001/eureka/
      defaultZone: http://${eureka.instance.hostname}:${server.port}/eureka/
```

- 主启动类EurekaMain7001.java

```java
package com.wangwren.springcloud;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;

@SpringBootApplication
//该注解表示7001就是EurekaServer，就是服务注册中心，由我来管理服务
@EnableEurekaServer
public class EurekaMain7001 {

    public static void main(String[] args) {
        SpringApplication.run(EurekaMain7001.class,args);
    }
}
```

#### 改造8001和80项目
- pom文件，都需要添加上EurekaClient
![](https://imagebed-1259286100.cos.ap-beijing.myqcloud.com/img/2021-03-19_01-23-40.png)

- yml文件中增加Eureka配置。注意配置中的`spring.application.name`必须要写
![](https://imagebed-1259286100.cos.ap-beijing.myqcloud.com/img/2021-03-19_01-24-39.png)

- 主启动类，SpringCloud E版本之后，不写`@EnableEurekaClient`也能将服务注册进Eureka。
![](https://imagebed-1259286100.cos.ap-beijing.myqcloud.com/img/2021-03-19_01-26-41.png)