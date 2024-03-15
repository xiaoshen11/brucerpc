package com.bruce.durpc.demo.api;

/**
 * @date 2024/3/7
 */
public interface UserService {

    User findById(int id);

    User findById(int id,String name);

    int getId(int id);

    String getName();

}
