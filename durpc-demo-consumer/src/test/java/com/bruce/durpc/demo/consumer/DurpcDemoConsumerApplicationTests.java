package com.bruce.durpc.demo.consumer;

import com.bruce.durpc.demo.provider.DurpcDemoProviderApplication;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;

@SpringBootTest
class DurpcDemoConsumerApplicationTests {

    static ApplicationContext context;

    @BeforeAll
    static void init(){
        context = SpringApplication.run(DurpcDemoProviderApplication.class,
                "--server.port=8094","--logging.level.com.bruce.durpc=info");
    }
    
    @Test
    void contextLoads() {
        System.out.println(" ====> contextLoads ......");
        
        
    }

    @AfterAll
    static void destroy(){
        SpringApplication.exit(context, () -> 1);
    }
    
}
