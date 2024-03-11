package com.bruce.durpc.demo.api;

/**
 * @date 2024/3/7
 */
public interface UserService {

    User findById(Integer id);

    int getId(int id);

    String getName();

}
