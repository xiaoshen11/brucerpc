package com.bruce.durpc.demo.provider;

import com.bruce.durpc.core.annotation.DuProvider;
import com.bruce.durpc.demo.api.User;
import com.bruce.durpc.demo.api.UserService;
import org.springframework.stereotype.Component;

/**
 * @date 2024/3/7
 */
@Component
@DuProvider
public class UserServiceImpl implements UserService {

    @Override
    public User findById(Integer id) {
        return new User(id,"Du-" + System.currentTimeMillis());
    }

    @Override
    public int getId(int id) {
        return id;
    }

    @Override
    public String getName() {
        return "Du-20240311";
    }
}
