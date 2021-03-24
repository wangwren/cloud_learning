package com.wangwren.springcloud.lb;

import org.springframework.cloud.client.ServiceInstance;

import java.util.List;

/**
 * 自定义负载均衡接口
 */
public interface LoadBalancer {

    ServiceInstance instance(List<ServiceInstance> serviceInstances);
}
