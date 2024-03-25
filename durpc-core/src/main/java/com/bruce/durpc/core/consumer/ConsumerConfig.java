package com.bruce.durpc.core.consumer;

import com.bruce.durpc.core.api.LoadBalancer;
import com.bruce.durpc.core.api.RegistryCenter;
import com.bruce.durpc.core.api.Router;
import com.bruce.durpc.core.cluster.RandomRobinLoadBalancer;
import com.bruce.durpc.core.meta.InstanceMeta;
import com.bruce.durpc.core.registry.zk.ZkRegistryCenter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;

/**
 * @date 2024/3/7
 */
@Configuration
@Slf4j
public class ConsumerConfig {

    @Value("${durpc.providers}")
    String servers;

    @Bean
    ConsumerBootstrap consumerBootstrap(){
        return new ConsumerBootstrap();
    }

    @Bean
    @Order(Integer.MIN_VALUE)
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
        return Router.Default;
    }

    @Bean(initMethod = "start",destroyMethod = "stop")
    public RegistryCenter consumer_rc(){
        return new ZkRegistryCenter();
    }

}
