package com.bruce.durpc.core.api;

import java.util.List;

/**
 * 负载均衡
 * @date 2024/3/16
 */
public interface LoadBalancer<T> {

    T choose(List<T> providers);

    LoadBalancer Default = p -> (p == null || p.size() == 0) ? null : p.get(0);
}
