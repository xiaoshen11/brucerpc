package com.bruce.durpc.demo.api;

import java.util.List;
import java.util.Map;

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

    List<Integer> getIdList(List<Integer> ids);

    Integer getIdByList(List<Integer> ids);

    Map<String,Integer> getIdByMap(Map<String,Integer> map);

    long[] getLongIds();

    boolean getBoolean(boolean b);

}
