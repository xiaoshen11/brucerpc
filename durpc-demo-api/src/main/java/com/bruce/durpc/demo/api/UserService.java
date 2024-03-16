package com.bruce.durpc.demo.api;

/**
 * @date 2024/3/7
 */
public interface UserService {

    User findById(int id);

    User findById(int id,String name);

    long getId(long id);

    long getId(float id);

    long getId(User user);

    String getName();

    String getName(int id);

    int[] getIds();

    int[] getIds(int[] ids);
    long[] getLongIds();

    boolean getBoolean(boolean b);

}
