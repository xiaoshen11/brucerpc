package com.bruce.durpc.core.consumer;

import com.bruce.durpc.core.provider.ProviderBootstrp;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;

/**
 * @date 2024/3/7
 */
@Configuration
public class ConsumerConfig {

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

}
