package com.bruce.durpc.core.config;

import com.bruce.durpc.core.api.RegistryCenter;
import com.bruce.durpc.core.provider.ProviderBootstrap;
import com.bruce.durpc.core.provider.ProviderInvoker;
import com.bruce.durpc.core.registry.du.DuRegistryCenter;
import com.bruce.durpc.core.transport.SpringBootTransport;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.core.annotation.Order;

/**
 * @date 2024/3/7
 */
@Configuration
@Slf4j
@Import({ProviderProperties.class, AppProperties.class, SpringBootTransport.class})
public class ProviderConfig {

    @Value("${server.port:8081}")
    private String port;

    @Bean
    ProviderBootstrap providerBootstrp(@Autowired AppProperties ap,
                                       @Autowired ProviderProperties pp){
        return new ProviderBootstrap(port, ap, pp);
    }

    @Bean
    ProviderInvoker providerInvoker(@Autowired ProviderBootstrap providerBootstrap){
        return new ProviderInvoker(providerBootstrap);
    }


    @Bean//(initMethod = "start",destroyMethod = "stop")
    public RegistryCenter provider_rc(){
        return new DuRegistryCenter();
    }

    @Bean
    @Order(Integer.MIN_VALUE)
    public ApplicationRunner providerBootstrap_runner(@Autowired ProviderBootstrap providerBootstrap){
        return x ->{
            log.info("providerBootstrap_runner ===== start");
            providerBootstrap.start();
            log.info("providerBootstrap_runner ===== end");
        };
    }
}
