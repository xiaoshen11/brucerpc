package com.bruce.durpc.core.provider;

import com.bruce.durpc.core.annotation.DuProvider;
import com.bruce.durpc.core.api.RpcRequest;
import com.bruce.durpc.core.api.RpcResponse;
import com.bruce.durpc.core.util.MethodUtils;
import com.sun.jdi.InvocationException;
import jakarta.annotation.PostConstruct;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

/**
 * @date 2024/3/7
 */
@Data
public class ProviderBootstrp implements ApplicationContextAware {

    ApplicationContext applicationContext;

    private Map<String,Object> skeleton = new HashMap<>();

    @PostConstruct
    public void buildProviders(){
        Map<String,Object> providers = applicationContext.getBeansWithAnnotation(DuProvider.class);
        providers.forEach((x,y) -> System.out.println(x));
        skeleton.putAll(providers);
        providers.values().forEach(x -> genInterface(x));

    }

    private void genInterface(Object x) {
        Class<?> itface = x.getClass().getInterfaces()[0];
        skeleton.put(itface.getCanonicalName(),x);
    }

    public RpcResponse invoke(RpcRequest request) {
        // 处理本地方法
        if(MethodUtils.checkLocalMethod(request.getMethod())){
            return null;
        }
        RpcResponse rpcResponse = new RpcResponse();
        Object bean = skeleton.get(request.getService());
        try {
            Method method = findMethod(bean.getClass(),request.getMethod());
            Object result = method.invoke(bean,request.getArgs());
            rpcResponse.setStatus(true);
            rpcResponse.setData(result);
        } catch (InvocationTargetException e) {
            rpcResponse.setEx(new RuntimeException(e.getTargetException().getMessage()));
        } catch (IllegalAccessException e) {
            rpcResponse.setEx(new RuntimeException(e.getMessage()));
        }
        return rpcResponse;
    }

    private Method findMethod(Class<?> aClass, String methodName) {
        for (Method method : aClass.getMethods()) {
            if(method.getName().equals(methodName)){
                return method;
            }
        }
        return null;
    }

}
