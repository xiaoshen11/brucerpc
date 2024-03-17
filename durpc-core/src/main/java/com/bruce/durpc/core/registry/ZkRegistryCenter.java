package com.bruce.durpc.core.registry;

import com.bruce.durpc.core.api.RegistryCenter;
import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.zookeeper.CreateMode;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

/**
 * @date 2024/3/17
 */
public class ZkRegistryCenter implements RegistryCenter {

    private CuratorFramework client = null;


    @Override
    public void start() {
        RetryPolicy retryPolicy = new ExponentialBackoffRetry(1000,3);
        client = CuratorFrameworkFactory.builder()
                .connectString("localhost:2181")
                .namespace("durpc")
                .retryPolicy(retryPolicy)
                .build();
        System.out.println("======> zk client started.");
        client.start();
    }

    @Override
    public void stop() {
        System.out.println("======> zk client stopped.");
        client.close();
    }

    @Override
    public void register(String service, String instance) {
        System.out.println("======> zk register to " + instance);
        String servicePath = "/" + service;
        try {
            // 创建服务的持久化节点
            if(client.checkExists().forPath(servicePath) == null){
                client.create().withMode(CreateMode.PERSISTENT).forPath(servicePath,"service".getBytes());
            }
            // 创建实例的临时节点
            String instancePath = servicePath + "/" + instance;
            if(client.checkExists().forPath(instancePath) == null){
                client.create().withMode(CreateMode.EPHEMERAL).forPath(instancePath,"provider".getBytes());
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void unregister(String service, String instance) {
        System.out.println("======> zk client unregister.");
        String servicePath = "/" + service;
        try {
            // 判断服务是否存在
            if(client.checkExists().forPath(servicePath) == null){
                return;
            }
            //
            String instancePath = servicePath + "/" + instance;
            client.delete().quietly().forPath(instancePath);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<String> fetchAll(String serviceName) {
        return null;
    }
}
