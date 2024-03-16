package com.bruce.durpc.core.consumer;

import com.bruce.durpc.core.api.LoadBalancer;
import com.bruce.durpc.core.api.RegistryCenter;
import com.bruce.durpc.core.api.Router;
import com.bruce.durpc.core.cluster.RandomLoadBalancer;
import com.bruce.durpc.core.cluster.RandomRibonLoadBalancer;
import com.bruce.durpc.core.provider.ProviderBootstrp;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;

import java.util.List;

/**
 * @date 2024/3/7
 */
@Configuration
public class ConsumerConfig {

    @Value("${durpc.providers}")
    String services;

    @Bean
    ConsumerBootstrap consumerBootstrap(){
        return new ConsumerBootstrap();
    }

    @Bean
    @Order(Integer.MIN_VALUE)
    public ApplicationRunner consumerBootstrap_runner(@Autowired ConsumerBootstrap consumerBootstrap){
        return x ->{
            System.out.println("consumerBootstrap_runner ===== start");
            consumerBootstrap.start();
            System.out.println("consumerBootstrap_runner ===== end");

        };
    }

    @Bean
    public LoadBalancer loadBalancer(){
        return new RandomRibonLoadBalancer();
    }

    @Bean
    public Router router(){
        return Router.Default;
    }

    @Bean(initMethod = "start",destroyMethod = "stop")
    public RegistryCenter consumer_rc(){
        return new RegistryCenter.StaicRegistryCenter(List.of(services.split(",")));
    }

}
