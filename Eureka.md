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

![](https://imagebed-1259286100.cos.ap-beijing.myqcloud.com/img/2021-03-21_13-49-01.png)

## EurekaServer集群环境构建
==互相注册，互相观望==
- 与单机差不多，主要是yml配置文件不同：
- 7001配置

![](https://imagebed-1259286100.cos.ap-beijing.myqcloud.com/img/2021-03-21_14-18-46.png)

- 7002配置
![](https://imagebed-1259286100.cos.ap-beijing.myqcloud.com/img/2021-03-21_14-19-30.png)

- 对应服务的配置(集群)

![](https://imagebed-1259286100.cos.ap-beijing.myqcloud.com/img/2021-03-21_14-36-23.png)

### 构建服务提供者集群
创建新的项目8002，与8001一样，多个服务提供者注册进Eureka。此时在消费者端，就不能讲访问地址写死了，==需要改为对应的提供服务的服务名称。==

![](https://imagebed-1259286100.cos.ap-beijing.myqcloud.com/img/2021-03-21_15-16-04.png)

但是只是按照上面的写法还不够，此时访问消费者，消费者通过服务名称找对应的提供者，这时会有两台提供者，它不知道要找哪一台来提供，此时就会报错。
这时就需要使用`@LoadBalanced`注解(Ribbon提供的能力)，赋予RestTemplate负载均衡的能力。这时才能通过微服务名称调用服务。
![](https://imagebed-1259286100.cos.ap-beijing.myqcloud.com/img/2021-03-21_15-19-08.png)

配置成功后，此时访问http://localhost/consumer/order/get/4，就会轮询调用服务提供者8001，8002.

Ribbon和Eureka整合后，Consumer可以直接调用服务而不用再关心地址和端口号，且该服务还有负载均衡功能了。

## actuator微服务信息完善
- 修改服务的配置文件yml

![](https://imagebed-1259286100.cos.ap-beijing.myqcloud.com/img/2021-03-21_15-41-06.png)

此时再访问EurekaServer可以看到服务主机名称已经被改了，并且点击也能够访问到对应的服务。访问服务时：`http://ip:port/actuator/health`，就可以看到此时服务的状态，这就是actuator提供的。
![](https://imagebed-1259286100.cos.ap-beijing.myqcloud.com/img/2021-03-21_15-42-33.png)

## Discovery服务发现
- 对于注册进Eureka里面的微服务，可以通过服务发现来获得该服务的信息。

![](https://imagebed-1259286100.cos.ap-beijing.myqcloud.com/img/2021-03-21_16-09-40.png)

主启动类中还要加上`@EnableDiscoveryClient`注解。

![](https://imagebed-1259286100.cos.ap-beijing.myqcloud.com/img/2021-03-21_16-11-06.png)

之后访问上面java代码的地址，就可以看到对应的信息：
![](https://imagebed-1259286100.cos.ap-beijing.myqcloud.com/img/2021-03-21_16-11-54.png)

## Eureka自我保护
### 故障现象
- 保护模式主要用于一组客户端和EurekaServer之间存在网络分区场景下的保护。一旦进入保护模式。==EurekaServer将会尝试保护其服务注册表中的信息，不再删除服务注册表中的数据，也就是不会注销任何微服务。==
- 如果在EurekaServer的首页看到以下这段提示，则说明Eureka进入了保护模式：
![](https://imagebed-1259286100.cos.ap-beijing.myqcloud.com/img/2021-03-21_16-24-53.png)

### 导致原因
- ==某时刻某一个微服务不可用了，Eureka不会立刻清理，依旧会对微服务的信息进行保护。==
- 属于CAP里面的AP分支。

#### 为什么会产生Eureka自我保护机制
为了防止EurekaClient可以正常运行，但是与EurekaServer网络不通情况下，EurekaServer不会立刻将EurekaClient服务剔除。
#### 什么是自我保护模式
默认情况下，如果EurekaServer在一定时间内没有接收到某个微服务实例的心跳，EurekaServer将会注销该实例(默认90秒)。但是当网络分区故障发生(延时、卡顿、拥挤)时，微服务与EurekaServer之间无法正常通信，以上行为可能变得非常危险了---因为微服务本身是健康的，==此时不应该注销这个微服务==。Eureka通过*自我保护模式*来解决这个问题。当EurekaServer节点在短时间内丢失过多客户端时(可能发生了网络分区故障)，那么这个节点就会进入自我保护模式。
![](https://imagebed-1259286100.cos.ap-beijing.myqcloud.com/img/2021-03-21_16-42-12.png)
==在自我保护模式中，EurekaServer会保护服务注册表中的信息，不再注销任何服务实例。==
它的设计哲学就是宁可保留错误的服务注册信息，也不盲目注销任何可能健康的服务实例。一句话：==好死不如赖活着。==

综上，自我保护模式是一种应对网络异常的安全保护措施，它的架构哲学是宁可同时保留所有微服务(健康的微服务和不健康的微服务都会保留)也不盲目注销任何健康的微服务。使用自我保护模式，可以让Eureka集群更加的健壮、稳定。

### 关闭自我保护(默认开启)
- 在EurekaServer端配置文件中配置
![](https://imagebed-1259286100.cos.ap-beijing.myqcloud.com/img/2021-03-21_17-28-49.png)

## Eureka2.0停更
