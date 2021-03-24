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
