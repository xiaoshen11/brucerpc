package com.bruce.durpc.core.registry.zk;

import com.bruce.durpc.core.api.RegistryCenter;
import com.bruce.durpc.core.meta.InstanceMeta;
import com.bruce.durpc.core.meta.ServiceMeta;
import com.bruce.durpc.core.registry.ChangeListener;
import com.bruce.durpc.core.registry.Event;
import lombok.SneakyThrows;
import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.cache.TreeCache;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.zookeeper.CreateMode;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Value;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @date 2024/3/17
 */
public class ZkRegistryCenter implements RegistryCenter {

    private CuratorFramework client = null;

    @Value("${durpc.zkServer}")
    String servers;

    @Value("${durpc.zkRoot}")
    String root;

    @Override
    public void start() {
        RetryPolicy retryPolicy = new ExponentialBackoffRetry(1000,3);
        client = CuratorFrameworkFactory.builder()
                .connectString(servers)
                .namespace(root)
                .retryPolicy(retryPolicy)
                .build();
        System.out.println("======> zk client started to server[" + servers + "/" + root + "].");
        client.start();
    }

    @Override
    public void stop() {
        System.out.println("======> zk client stopped.");
        client.close();
    }

    @Override
    public void register(ServiceMeta service, InstanceMeta instance) {
        String servicePath = "/" + service.toPath();
        try {
            // 创建服务的持久化节点
            if(client.checkExists().forPath(servicePath) == null){
                client.create().withMode(CreateMode.PERSISTENT).forPath(servicePath,"service".getBytes());
            }
            // 创建实例的临时节点
            String instancePath = servicePath + "/" + instance.toPath();
            System.out.println("======> register to zk: " + instancePath);
            client.create().withMode(CreateMode.EPHEMERAL).forPath(instancePath,"provider".getBytes());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void unregister(ServiceMeta service, InstanceMeta instance) {
        String servicePath = "/" + service.toPath();
        try {
            // 判断服务是否存在
            if(client.checkExists().forPath(servicePath) == null){
                return;
            }
            //
            String instancePath = servicePath + "/" + instance.toPath();
            System.out.println("======> unregister from zk: " + instancePath);
            client.delete().quietly().forPath(instancePath);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<InstanceMeta> fetchAll(ServiceMeta service) {
        String servicePath = "/" + service.toPath();
        try {
            List<String> nodes = client.getChildren().forPath(servicePath);
            System.out.println("====> fetchAll from zk: " + servicePath);
            nodes.forEach(System.out::println);
            return mapInstances(nodes);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @NotNull
    private static List<InstanceMeta> mapInstances(List<String> nodes) {
        return nodes.stream().map(x -> {
            String[] strs = x.split("_");
            return InstanceMeta.http(strs[0], Integer.valueOf(strs[1]));
        }).collect(Collectors.toList());
    }

    @SneakyThrows
    @Override
    public void subscribe(ServiceMeta service, ChangeListener listener) {
        final TreeCache cache = TreeCache.newBuilder(client, "/" + service.toPath())
                .setCacheData(true)
                .setMaxDepth(2)
                .build();
        cache.getListenable().addListener(
                (curator, event) -> {
                    // 有任何节点变动这里会执行
                    System.out.println("zk subscribe event: " + event);
                    List<InstanceMeta> nodes = fetchAll(service);
                    listener.fire(new Event(nodes));
                }
        );
        cache.start();
    }
}
