package com.bruce.durpc.demo.consumer;

import com.bruce.durpc.core.test.TestZKServer;
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

    static TestZKServer zkServer = new TestZKServer();

    @BeforeAll
    static void init(){
        System.out.println("======================== ");
        System.out.println("======================== ");
        System.out.println("======================== ");

        zkServer.start();

        context = SpringApplication.run(DurpcDemoProviderApplication.class,
                "--server.port=8094", "--durpc.zkServer=localhost:2182",
                "--logging.level.com.bruce.durpc=info");
    }
    
    @Test
    void contextLoads() {
        System.out.println(" ====> contextLoads ......");
        
        
    }

    @AfterAll
    static void destroy(){
        SpringApplication.exit(context, () -> 1);
        zkServer.stop();
    }
    
}
