package com.bruce.durpc.core.provider;

import com.bruce.durpc.core.api.RegistryCenter;
import com.bruce.durpc.core.registry.zk.ZkRegistryCenter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;

/**
 * @date 2024/3/7
 */
@Configuration
@Slf4j
public class ProviderConfig {

    @Bean
    ProviderBootstrp providerBootstrp(){
        return new ProviderBootstrp();
    }

    @Bean
    ProviderInvoker providerInvoker(@Autowired ProviderBootstrp providerBootstrp){
        return new ProviderInvoker(providerBootstrp);
    }


    @Bean//(initMethod = "start",destroyMethod = "stop")
    public RegistryCenter provider_rc(){
        return new ZkRegistryCenter();
    }

    @Bean
    @Order(Integer.MIN_VALUE)
    public ApplicationRunner providerBootstrap_runner(@Autowired ProviderBootstrp providerBootstrp){
        return x ->{
            log.info("providerBootstrap_runner ===== start");
            providerBootstrp.start();
            log.info("providerBootstrap_runner ===== end");
        };
    }
}
