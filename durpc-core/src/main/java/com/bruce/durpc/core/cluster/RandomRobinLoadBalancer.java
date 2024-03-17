package com.bruce.durpc.core.cluster;

import com.bruce.durpc.core.api.LoadBalancer;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @date 2024/3/16
 */
public class RandomRobinLoadBalancer<T> implements LoadBalancer<T> {

    AtomicInteger integer = new AtomicInteger(0);

    @Override
    public T choose(List<T> providers) {
        if(providers == null || providers.size() == 0){
            return null;
        }
        if(providers.size() == 1){
            return providers.get(0);
        }
        return providers.get((integer.getAndIncrement()&0x7fffffff) % providers.size());
    }
}
