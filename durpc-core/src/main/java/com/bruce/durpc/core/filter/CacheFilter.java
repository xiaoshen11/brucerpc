package com.bruce.durpc.core.filter;

import com.bruce.durpc.core.api.Filter;
import com.bruce.durpc.core.api.RpcRequest;
import com.bruce.durpc.core.api.RpcResponse;
import org.springframework.core.annotation.Order;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @date 2024/3/25
 */
@Order
public class CacheFilter implements Filter {

    static Map<String,Object> cache = new ConcurrentHashMap<>();

    @Override
    public Object preFilter(RpcRequest request) {
        return cache.get(request.toString());
    }

    @Override
    public Object postFilter(RpcRequest request, RpcResponse response,Object result) {
        cache.putIfAbsent(request.toString(),result);
        return result;
    }
}
