package com.bruce.durpc.core.provider;

import com.bruce.durpc.core.annotation.DuProvider;
import com.bruce.durpc.core.api.RpcRequest;
import com.bruce.durpc.core.api.RpcResponse;
import com.bruce.durpc.core.meta.ProviderMeta;
import com.bruce.durpc.core.util.MethodUtils;
import jakarta.annotation.PostConstruct;
import lombok.Data;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

/**
 * @date 2024/3/7
 */
@Data
public class ProviderBootstrp implements ApplicationContextAware {

    ApplicationContext applicationContext;

    private MultiValueMap<String, ProviderMeta> skeleton = new LinkedMultiValueMap<>();

    @PostConstruct
    public void start(){
        Map<String,Object> providers = applicationContext.getBeansWithAnnotation(DuProvider.class);
        providers.forEach((x,y) -> System.out.println(x));
//        skeleton.putAll(providers);
        providers.values().forEach(x -> genInterface(x));

    }

    private void genInterface(Object x) {
        Class<?> itface = x.getClass().getInterfaces()[0];
        Method[] methods = itface.getMethods();
        for (Method method : methods) {
            if(MethodUtils.checkLocalMethod(method)){
                continue;
            }
            createProvider(itface,x,method);
        }
    }

    private void createProvider(Class<?> itface, Object x, Method method) {
        ProviderMeta meta = new ProviderMeta();
        meta.setMethod(method);
        meta.setServiceImpl(x);
        meta.setMethodSign(MethodUtils.methodSign(method));
        System.out.println("create a provider: " + meta);
        skeleton.add(itface.getCanonicalName(),meta);
    }

    public RpcResponse invoke(RpcRequest request) {
//        // 处理本地方法
//        if(MethodUtils.checkLocalMethod(request.getMethodSign())){
//            return null;
//        }
        RpcResponse rpcResponse = new RpcResponse();
        List<ProviderMeta> providerMetaList = skeleton.get(request.getService());
        try {

            ProviderMeta providerMeta = findProviderMeta(providerMetaList,request.getMethodSign());
            if(providerMeta != null){
                Method method = providerMeta.getMethod();
                Object result = method.invoke(providerMeta.getServiceImpl(),request.getArgs());
                rpcResponse.setStatus(true);
                rpcResponse.setData(result);
            }
        } catch (InvocationTargetException e) {
            rpcResponse.setEx(new RuntimeException(e.getTargetException().getMessage()));
        } catch (IllegalAccessException e) {
            rpcResponse.setEx(new RuntimeException(e.getMessage()));
        }
        return rpcResponse;
    }

    private ProviderMeta findProviderMeta(List<ProviderMeta> providerMetaList, String methodSign) {
        Optional<ProviderMeta> optional = providerMetaList.stream().filter(x -> x.getMethodSign().equals(methodSign)).findFirst();
        return optional.orElse(null);
    }

}
