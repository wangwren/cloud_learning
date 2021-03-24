# Ribbon负载均衡服务调用
## 介绍
SpringCloud Ribbon是基于Netflix Ribbon实现的一套**客户端**负载均衡工具。

Ribbon是Netflix发布的开源项目，主要功能是提供**客户端的软件负载均衡算法和服务调用**。Ribbon客户端组件提供一系列完善的配置项，如连接超时，重试等。简单说，就是在配置文件中列出Load Balance(简称LB)后面所有的机器，Ribbon会自动的帮助你基于某种规则(如简单轮询，随机连接等)去连接这些机器。我们很容易使用Ribbon实现自定义的负载均衡算法。

**Ribbon目前也进入维护模式。**

### LB负载均衡(Load Balance)是什么
就是将用户的请求平摊的分配到多个服务上，从而达到系统的HA(高可用)。常见的负载均衡有软件Nginx，LVS，硬件F5等。

### Ribbon本地负载均衡客户端 VS Nginx负载均衡区别
- Nginx是服务器负载均衡，客户端所有请求都会交给Nginx，然后由Nginx实现转发请求。即负载均衡是由服务端实现的。
- Ribbon本地负载均衡，在调用微服务接口的时候，会在注册中心上获取注册信息服务列表之后缓存到JVM本地，从而在本地实现RPC远程服务调用技术。

- 集中式LB：即在服务的消费方和提供方之间提供独立的LB设施(可以是硬件，如F5，也可以是软件，如Nginx)，由该设施负载把访问请求通过某种策略转发至服务的提供方。
- 进程内LB：将LB逻辑集成到消费方，消费方从服务注册中心获知有哪些地址可用，然后自己再从这些地址中选择出一个合适的服务器。**Ribbon就属于进程内LB，**它只是一个类库，集成于消费方进程，消费方通过它来获取到服务提供方的地址。

**一句话：负载均衡 + RestTemplate调用，就是Ribbon**



## 实现
Ribbon其实就是一个软负载均衡的客户端组件，它可以和其他所需请求的客户端结合使用，和Eureka结合只是其中的一个实例。
![](https://imagebed-1259286100.cos.ap-beijing.myqcloud.com/img/2021-03-22_23-14-41.png)

- Ribbon在工作时分两步：
    - 第一步先选择EurekaServer，它优先选择在同一个区域内负载较少的server。
    - 第二步再根据用户指定的策略，再从server取到的服务注册列表中选择一个地址。(其中Ribbon提供了多种策略：比如轮询、随机和根据响应时间加权)

- pom.xml
之前写样例Eureka80项目(cloud-consumer-order80)时，没有引入`spring-cloud-starter-ribbon`也可以使用Ribbon。原因就是引入新版的Eureka2.0`spring-cloud-starter-netflix-eureka-client`自带了`spring-cloud-starter-ribbon`引用。
  

![](https://imagebed-1259286100.cos.ap-beijing.myqcloud.com/img/2021-03-22_23-25-12.png)

![](https://imagebed-1259286100.cos.ap-beijing.myqcloud.com/img/2021-03-22_23-25-55.png)

如果非要加也可以，如下：
```xml
<!--ribbon;不加也可以 Eureka中带了-->
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-starter-netflix-ribbon</artifactId>
</dependency>
```

### RestTemplate
- `getForObject/getForEntity`
    - `getForObject`:返回对象为响应体中数据转化成的对象，基本上可以理解为Json。
    - `getForEntity`:返回对象为ResponseEntity对象，包含了响应中的一些重要信息，比如响应头，响应状态码，响应体等。

```java
@GetMapping("getEntity/{id}")
public CommonResult<Payment> getPaymentForEntity(@PathVariable("id") Long id) {
    ResponseEntity<CommonResult> entity = restTemplate.getForEntity(PAYMENT_URL + "/payment/get/" + id, CommonResult.class);

    if (entity.getStatusCode().is2xxSuccessful()) {
        return entity.getBody();
    } else {
        return new CommonResult<>(500,"系统异常");
    }
}
```

## Ribbon核心组件IRule
### IRule
根据特定算法从服务列表中选取一个要访问的服务。
![](https://imagebed-1259286100.cos.ap-beijing.myqcloud.com/img/2021-03-22_23-51-49.png)

- `com.netflix.loadbalancer.RoundRobinRule` 轮询
- `com.netflix.loadbalancer.RandomRule` 随机
- `com.netflix.loadbalancer.RetryRule` 先按照RoundRobinRule的策略获取服务，如果获取服务失败则在指定时间内进行重试，获取可用的服务。
- `com.netflix.loadbalancer.WeightedResponseTimeRule` 对RoundRobinRule的扩展，响应速度越快的实例选择权重越大，越容易被选择。
- `com.netflix.loadbalancer.BestAvailableRule` 会先过滤掉由于多次访问故障而处于断路器跳闸状态的服务，然后选择一个并发量最小的服务。
- `com.netflix.loadbalancer.AvailabilityFilteringRule` 先过滤掉故障实例，再选择并发较小的实例。
- `com.netflix.loadbalancer.ZoneAvoidanceRule` 默认规则，复合判断server所在区域的性能和server的可用性选择服务器。

#### 如何替换负载规则
- 修改cloud-consumer-order80
- 官方提示：自定义配置类不能放在`@ComponentScan`所扫描的当前包下以及子包下，否则自定义的配置类就会被所有的Ribbon客户端所共享，达不到特殊化定制的目的了。
- 新建规则类
![](https://imagebed-1259286100.cos.ap-beijing.myqcloud.com/img/2021-03-23_00-11-37.png)

- **在主启动类上添加`@RibbonClient`**
![](https://imagebed-1259286100.cos.ap-beijing.myqcloud.com/img/2021-03-23_00-15-47.png)

## Ribbon负载均衡算法
### 轮询原理
rest接口第几次请求数 % 服务器集群总数量 = 实际调用服务器位置下标(每次服务器重启后rest接口技术从1开始)。==实际上就是取模。==

### 自定义负载均衡(轮询，仿照RoundRobinRule)
```java
package com.wangwren.springcloud.lb;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 自己实现负载均衡接口
 * 仿照RoundRobinRule实现
 */
@Component
@Slf4j
public class MyLoadBalancerImpl implements LoadBalancer {

    /**
     * 原子类
     */
    private AtomicInteger atomicInteger = new AtomicInteger(0);

    private int getNext() {

        int current;
        int next;

        //自旋锁(多线程访问时)
        do {
            //获取到当前原子类的值
            current = atomicInteger.get();

            //当前的请求次数
            next = current == Integer.MAX_VALUE ? 0 : current + 1;

            //比较当前值atomicInteger和预期值是否一致，如果一致atomicInteger值变为next值，并返回true，跳出循环
            //否则就一直循环(自旋)
        } while (!atomicInteger.compareAndSet(current,next));

        log.info("******当前请求次数 is " + next);

        return next;
    }


    /**
     *
     * @param serviceInstances 服务实例
     * @return
     */
    @Override
    public ServiceInstance instance(List<ServiceInstance> serviceInstances) {

        //取余，获取到对应服务实例的下标返回
        int model = getNext() % serviceInstances.size();

        return serviceInstances.get(model);
    }
}
```

- 使用:OrderController.java
    - 使用前将配置RestTemplate的@LoadBalance注解去掉。

```java
/**
 * 注入自定义的loadBalance
 */
@Autowired
private MyLoadBalancerImpl myLoadBalancer;

@Autowired
private DiscoveryClient discoveryClient;

@GetMapping("getServerPort")
public String getServerPort() {
    List<ServiceInstance> instances = discoveryClient.getInstances("CLOUD-PAYMENT-SERVICE");

    ServiceInstance instance = myLoadBalancer.instance(instances);
    String uri = instance.getUri().toString();
    log.info("uri is " + uri);

    //payment/port 自己在服务端实现个方法就行，获取服务端口号，容易看效果
    return restTemplate.getForObject(uri + "/payment/port",String.class);
}
```