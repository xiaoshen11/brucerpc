package com.bruce.durpc.demo.provider;

import com.bruce.durpc.core.annotation.DuProvider;
import com.bruce.durpc.demo.api.Order;
import com.bruce.durpc.demo.api.OrderService;
import com.bruce.durpc.demo.api.User;
import com.bruce.durpc.demo.api.UserService;
import org.springframework.stereotype.Component;

/**
 * @date 2024/3/7
 */
@Component
@DuProvider
public class OrderServiceImpl implements OrderService {

    @Override
    public Order findById(Integer id) {
        if(id == 404){
            throw new RuntimeException("findById exception 404");
        }

        return new Order(id.longValue(),16.8f);
    }
}
