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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

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

    @RequestMapping("/")
    public User findById(int id){
        return userService.findById(id);
    }

    @Bean
    public ApplicationRunner consumerRunner(){
        return x -> {
            User[] users = new User[3];
            users[0] = new User(1,"bruce");
            users[1] = new User(2,"bruce1");
            users[2] = new User(3,"bruce2");
            System.out.println("getListByList(List<User> users) ====> ");
            userService.getListByList(Arrays.asList(users)).forEach(u -> System.out.println(u.getId()));

            System.out.println("getListByList(User[] users) ====> ");
            Arrays.stream(userService.getListByList(users)).forEach(u -> System.out.println(u.getName()));

            System.out.println(userService.getIdByList(Arrays.asList(new Integer[]{4,5,6})));
            Map map = new HashMap<String,Integer>();
            map.put("id",6);
            System.out.println(userService.getIdByMap(map));
            userService.getIdList(Arrays.asList(new Integer[]{4,5,6})).forEach(System.out::println);
            System.out.println(userService.getBoolean(false));
            Arrays.stream(userService.getIds()).forEach(System.out::println);
            Arrays.stream(userService.getLongIds()).forEach(System.out::println);
            Arrays.stream(userService.getIds(new int[]{1,2,3})).forEach(System.out::println);

            System.out.println(userService.getId(11.0f));
            System.out.println(userService.getId(new User(100,"Du")));

            User user = userService.findById(11);
            System.out.println("RPC result userService.findById(11) = " + user);

            User user2 = userService.findById(11,"bruce");
            System.out.println("RPC result userService.findById(11,\"bruce\") = " + user2);

            System.out.println(userService.getName());
            System.out.println(userService.getName(123));

            Order order = orderService.findById(1);
            System.out.println("RPC result orderService.findById(1) = " + order);

//              Order order404 = orderService.findById(404);
//            System.out.println("RPC result orderService.findById(404) = " + order404);
        };
    }

}
