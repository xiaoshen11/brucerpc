package com.bruce.durpc.core.consumer;

import com.bruce.durpc.core.annotation.DuConsumer;
import com.bruce.durpc.core.api.Filter;
import com.bruce.durpc.core.api.LoadBalancer;
import com.bruce.durpc.core.api.RegistryCenter;
import com.bruce.durpc.core.api.Router;
import com.bruce.durpc.core.api.RpcContext;
import com.bruce.durpc.core.meta.InstanceMeta;
import com.bruce.durpc.core.meta.ServiceMeta;
import lombok.Data;
import com.bruce.durpc.core.util.MethodUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.EnvironmentAware;
import org.springframework.core.env.Environment;

import java.lang.reflect.Field;
import java.lang.reflect.Proxy;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @date 2024/3/10
 */
@Data
@Slf4j
public class ConsumerBootstrap implements ApplicationContextAware, EnvironmentAware {

    ApplicationContext applicationContext;

    Environment environment;

    private Map<String,Object> stub = new HashMap<>();

    @Value("${app.id}")
    private String app;

    @Value("${app.namespace}")
    private String namespace;

    @Value("${app.env}")
    private String env;

    public void start() {
        Router<InstanceMeta> router = applicationContext.getBean(Router.class);
        LoadBalancer<InstanceMeta> loadBalancer = applicationContext.getBean(LoadBalancer.class);
        RegistryCenter rc = applicationContext.getBean(RegistryCenter.class);

        List<Filter> filters = applicationContext.getBeansOfType(Filter.class).values().stream().toList();

        RpcContext context = new RpcContext();
        context.setRouter(router);
        context.setLoadBalancer(loadBalancer);
        context.setFilters(filters);

        String[] names = applicationContext.getBeanDefinitionNames();
        for (String name : names) {
            Object bean = applicationContext.getBean(name);
            List<Field> fields = MethodUtils.findAnnotatedField(bean.getClass(), DuConsumer.class);

            fields.stream().forEach(f -> {
                log.info("=====" + f.getType());
                Class<?> service = f.getType();
                String serviceName = service.getCanonicalName();
                Object consumer = stub.get(serviceName);
                if(consumer == null){
                    consumer = createFromRegistry(service, context, rc);
                    stub.put(serviceName,consumer);
                }
                f.setAccessible(true);
                try {
                    f.set(bean,consumer);
                } catch (IllegalAccessException e) {
                    throw new RuntimeException(e);
                }
            });

        }
    }

    private Object createFromRegistry(Class<?> service, RpcContext context, RegistryCenter rc) {
        ServiceMeta serviceMeta = ServiceMeta.builder().app(app).env(env).namespace(namespace).name(service.getCanonicalName()).build();
        List<InstanceMeta> providers = rc.fetchAll(serviceMeta);
        log.info("====>  map to provider: ");
        providers.forEach(System.out::println);
        rc.subscribe(serviceMeta, event -> {
            providers.clear();
            providers.addAll(event.getData());
        });
        return createConsumer(service, context, providers);
    }

    private List<String> mapUrl(List<String> nodes){
        return nodes.stream().map(x -> "http://" + x.replace("_",":")).collect(Collectors.toList());
    }

    private Object createConsumer(Class<?> service, RpcContext context, List<InstanceMeta> providers) {
        return Proxy.newProxyInstance(service.getClassLoader(),new Class[]{service},new DuInvocationHandler(service, context, providers));
    }




}
