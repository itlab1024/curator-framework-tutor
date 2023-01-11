package com.itlab1024.curator.connection;

import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;

public class CreateNodeTest {
    String connectString = "172.30.140.89:2181";
    RetryPolicy retryPolicy = new ExponentialBackoffRetry(1000, 3);

    /**
     * 创建节点
     */
    @Test
    public void testCreate1() throws Exception {
        CuratorFramework curatorFramework = CuratorFrameworkFactory.newClient(connectString, retryPolicy);
        curatorFramework.start();
        curatorFramework.create().forPath("/test");
    }
    @Test
    public void testCreateDefaultData() throws Exception {
        CuratorFrameworkFactory.Builder builder = CuratorFrameworkFactory.builder().defaultData("默认值".getBytes(StandardCharsets.UTF_8));
        CuratorFramework client = builder.connectString(connectString).retryPolicy(retryPolicy).build();
        client.start();
        client.create().forPath("/defaultDataTest");
    }

    @Test
    public void testCreate2() throws Exception {
        CuratorFramework curatorFramework = CuratorFrameworkFactory.newClient(connectString, retryPolicy);
        curatorFramework.start();
        curatorFramework.create().forPath("/test2", "用户自己设置的值".getBytes(StandardCharsets.UTF_8));
    }
}
