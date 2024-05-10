package com.bruce.durpc.core.config;

import com.bruce.durpc.core.api.Filter;
import com.bruce.durpc.core.api.LoadBalancer;
import com.bruce.durpc.core.api.RegistryCenter;
import com.bruce.durpc.core.api.Router;
import com.bruce.durpc.core.api.RpcContext;
import com.bruce.durpc.core.cluster.GrayRouter;
import com.bruce.durpc.core.cluster.RandomRobinLoadBalancer;
import com.bruce.durpc.core.consumer.ConsumerBootstrap;
import com.bruce.durpc.core.filter.ContextParameterFilter;
import com.bruce.durpc.core.meta.InstanceMeta;
import com.bruce.durpc.core.registry.du.DuRegistryCenter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.core.annotation.Order;

import java.util.List;

/**
 * @date 2024/3/7
 */
@Configuration
@Slf4j
@Import({AppProperties.class, ConsumerProperties.class})
public class ConsumerConfig {

    @Autowired
    AppProperties appProperties;

    @Autowired
    ConsumerProperties consumerProperties;

    @Bean
    ConsumerBootstrap consumerBootstrap(){
        return new ConsumerBootstrap();
    }

    @Bean
    @Order(Integer.MIN_VALUE + 1)
    public ApplicationRunner consumerBootstrap_runner(@Autowired ConsumerBootstrap consumerBootstrap){
        return x ->{
            log.info("consumerBootstrap_runner ===== start");
            consumerBootstrap.start();
            log.info("consumerBootstrap_runner ===== end");

        };
    }

    @Bean
    public LoadBalancer<InstanceMeta> loadBalancer(){
        return new RandomRobinLoadBalancer();
    }

    @Bean
    public Router<InstanceMeta> router(){
        return new GrayRouter(consumerProperties.getGrayRatio());
    }

    @Bean(initMethod = "start",destroyMethod = "stop")
    @ConditionalOnMissingBean
    public RegistryCenter consumer_rc(){
        return new DuRegistryCenter();
    }

    @Bean
    public Filter defaultFilter(){
        return new ContextParameterFilter();
    }

//    @Bean
//    public Filter filter(){
////        return Filter.Default;
//        return new CacheFilter();
//    }


//    @Bean
//    public Filter filter2(){
//        return new MockFilter();
//    }

    @Bean
//    @RefreshScope
    public RpcContext creteContext(@Autowired Router router,
                                   @Autowired LoadBalancer loadBalancer,
                                   @Autowired List<Filter> filters) {
        RpcContext context = new RpcContext();
        context.setRouter(router);
        context.setLoadBalancer(loadBalancer);
        context.setFilters(filters);
        context.getParameters().put("app.id", appProperties.getId());
        context.getParameters().put("app.namespace", appProperties.getNamespace());
        context.getParameters().put("app.env", appProperties.getEnv());
        context.setConsumerProperties(consumerProperties);
        return context;
    }
}
