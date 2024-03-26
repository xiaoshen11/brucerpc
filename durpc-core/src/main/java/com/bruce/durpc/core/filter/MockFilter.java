package com.bruce.durpc.core.filter;

import com.bruce.durpc.core.api.Filter;
import com.bruce.durpc.core.api.RpcRequest;
import com.bruce.durpc.core.api.RpcResponse;
import com.bruce.durpc.core.util.MethodUtils;
import com.bruce.durpc.core.util.MockUtils;
import lombok.SneakyThrows;

import java.lang.reflect.Method;
import java.util.Arrays;

/**
 * @date 2024/3/25
 */
public class MockFilter implements Filter {

    @SneakyThrows
    @Override
    public Object preFilter(RpcRequest request) {
        Class service = Class.forName(request.getService());
        Method method = findMethod(service,request.getMethodSign());
        Class clazz = method.getReturnType();
        return MockUtils.mock(clazz);
    }

    private Method findMethod(Class service, String methodSign) {
        return Arrays.stream(service.getMethods())
                .filter(method -> !MethodUtils.checkLocalMethod(method))
                .filter(method -> methodSign.equals(MethodUtils.methodSign(method)))
                .findFirst().orElse(null);
    }

    @Override
    public Object postFilter(RpcRequest request, RpcResponse response, Object result) {
        return null;
    }
}
