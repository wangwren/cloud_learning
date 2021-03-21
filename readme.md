# SpringCloud
## 微服务
微服务架构是一种架构模式，它提倡将单一应用程序划分成一组小的服务，服务之间互相协调、互相配合，为用户提供最终价值。每个服务运行在其独立的进程中，服务与服务之间采用轻量级的通信机制互相协作(通常是基于HTTP协议的RESTful API)。每个服务都围绕着具体业务进行构建，并且能够被独立的部署到生产环境、类生产环境等。另外，应当尽量避免统一的、集中式的服务管理机制，对具体的一个服务而言，应根据上下文，选择合适的语言、工具对其进行构建。

![](https://imagebed-1259286100.cos.ap-beijing.myqcloud.com/img/2021-03-15_22-50-33.png)

一个主流的微服务架构：
![](https://imagebed-1259286100.cos.ap-beijing.myqcloud.com/img/2021-03-15_22-56-09.png)

以前的主流，现在有的已经被替换了。
![](https://imagebed-1259286100.cos.ap-beijing.myqcloud.com/img/2021-03-15_22-57-31.png)

最新组件替换
![](https://imagebed-1259286100.cos.ap-beijing.myqcloud.com/img/2021-03-15_23-35-40.png)

[查看SpringCloud和SpringBoot版本选择的对应关系](https://start.spring.io/actuator/info)


## 服务注册与发现
### [Eureka](https://github.com/wangwren/cloud_learning/blob/master/Eureka.md)

### [Consul](https://github.com/wangwren/cloud_learning/blob/master/Consul.md)

### Eureka、Zookeeper、Consul三个注册中心异同点
| 组件名       | 语言   | CAP | 服务健康检查 | 对外暴露接口   | SpringCloud集成 |
|:---------:|:----:|:---:|:------:|:--------:|:-------------:|
| Eureka    | Java | **AP**  | 可配支持   | HTTP     | 已集成           |
| Zookeeper | Java | **CP**  | 支持     | 客户端      | 已集成           |
| Consul    | Go   | **CP**  | 支持     | HTTP/DNS | 已集成           |

- CAP
    - C:Consistency(强一致性)
    - A:Availability(可用性)
    - P:Partition tolerance(分区容错性)
    - CAP理论关注粒度是数据。
- **最多只能同时较好的满足两个**
- CAP理论的核心是：==一个分布式系统不可能同时很好的满足一致性、可用性和分区容错性这三个需求==，因此，根据CAP原理将NoSQL数据分成了满足CA原则、满足CP原则和满足AP原则三大类：
    - CA：单点集群，满足一致性，可用性的系统，通常在可扩展性上不太强大。
    - CP：满足强一致性，分区容错性的系统，通常性能不是特别高。
    - AP：满足可用性，分区容错性的系统，通常可能对一致性要求低一些。
## Maven工程复习
### Maven中的`DependencyManagement`和`Dependencies`的区别
- DependencyManagement：
    - Maven使用dependencyManagement元素来提供了一种管理依赖版本号的方式。==通常会在一个组织或者项目的最顶层父POM中看到dependencyManagement元素==。
    - 使用pom.xml中的dependencyManagement元素能让所有在子项目中引用一个依赖而不用显示的列出版本号。Maven会沿着父子层次向上走，直到找到一个拥有dependencyManagement元素的项目，然后它就会使用这个dependencyManagement元素指定的版本号。
    - 这样的好处就是：如果有多个子项目都引用同一个依赖，则可以避免在每个使用的子项目里都声明一个版本号，这样当想升级或切换到另一个版本时，只需要在顶层父容器里更新，而不需要一个个子项目的修改；另外如果某个子项目需要另外的一个版本，只需要独立声明version即可。
    - ==dependencyManagement里只是声明依赖，并不实现引入，因此子项目需要显示的声明需要用的依赖。==
    - 如果子项目中指定了版本号，那么会使用子项目中的版本号。

### Maven中跳过单元测试
 ![](https://imagebed-1259286100.cos.ap-beijing.myqcloud.com/img/2021-03-16_00-27-31.png)


### 其他
父工程创建完成执行`mvn:install`将父工程发布到仓库方便子工程继承。 
