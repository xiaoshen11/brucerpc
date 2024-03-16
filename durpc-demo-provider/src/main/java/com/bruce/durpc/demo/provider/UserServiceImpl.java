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
    public User findById(int id) {
        return new User(id,"Du-" + System.currentTimeMillis());
    }

    @Override
    public User findById(int id, String name) {
        return new User(id,"Du-" + name);
    }

    @Override
    public long getId(long id) {
        return id;
    }

    @Override
    public long getId(float id) {
        return 1;
    }

    @Override
    public long getId(User user) {
        return user.getId().longValue();
    }

    @Override
    public String getName() {
        return "Du-20240311";
    }

    @Override
    public String getName(int id) {
        return "bruce-" + id;
    }

    @Override
    public int[] getIds() {
        return new int[]{1,2,3};
    }

    @Override
    public int[] getIds(int[] ids) {
        return ids;
    }

    @Override
    public long[] getLongIds() {
        return new long[]{1,2,3};
    }

    @Override
    public boolean getBoolean(boolean b) {
        return false;
    }
}
