package com.bruce.durpc.core.filter;

import com.bruce.durpc.core.api.Filter;
import com.bruce.durpc.core.api.RpcContext;
import com.bruce.durpc.core.api.RpcRequest;
import com.bruce.durpc.core.api.RpcResponse;

import java.util.Map;

/**
 * 处理上下文参数.
 * @date 2024/5/10
 */
public class ContextParameterFilter implements Filter {

    @Override
    public Object preFilter(RpcRequest request) {
        Map<String, String> params = RpcContext.ContextParameters.get();
        if(!params.isEmpty()){
            request.getParams().putAll(params);
        }
        return null;
    }

    @Override
    public Object postFilter(RpcRequest request, RpcResponse response, Object result) {
        RpcContext.ContextParameters.get().clear();
        return result;
    }
}
