package com.bruce.durpc.core.cluster;

import com.bruce.durpc.core.api.LoadBalancer;

import java.util.List;
import java.util.Random;

/**
 * @date 2024/3/16
 */
public class RandomLoadBalancer<T> implements LoadBalancer<T> {

    Random random = new Random();

    @Override
    public T choose(List<T> providers) {
        if(providers == null || providers.size() == 0){
            return null;
        }
        if(providers.size() == 1){
            return providers.get(0);
        }
        return providers.get(random.nextInt(providers.size()));
    }
}
