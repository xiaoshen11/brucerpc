package com.bruce.durpc.demo.consumer;

import com.bruce.durpc.core.annotation.DuConsumer;
import com.bruce.durpc.core.consumer.ConsumerConfig;
import com.bruce.durpc.demo.api.Order;
import com.bruce.durpc.demo.api.OrderService;
import com.bruce.durpc.demo.api.User;
import com.bruce.durpc.demo.api.UserService;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;

@SpringBootApplication
@Import(ConsumerConfig.class)
@RestController
public class DurpcDemoConsumerApplication {

    @DuConsumer
    UserService userService;

    @DuConsumer
    OrderService orderService;

    public static void main(String[] args) {
        SpringApplication.run(DurpcDemoConsumerApplication.class, args);
    }


    @Bean
    public ApplicationRunner consumerRunner(){
        return x -> {
//            User user = userService.findById(1);
//            System.out.println("RPC result userService.findById(1) = " + user);

            User user = userService.findById(11);
            System.out.println("RPC result userService.findById(11) = " + user);

            User user2 = userService.findById(11,"bruce");
            System.out.println("RPC result userService.findById(11,\"bruce\") = " + user2);

            String name = userService.getName();
            System.out.println("RPC result userService.getName() = " + name);

//            Order order = orderService.findById(1);
//            System.out.println("RPC result orderService.findById(1) = " + order);

  //            Order order404 = orderService.findById(404);
//            System.out.println("RPC result orderService.findById(404) = " + order404);
        };
    }

}
