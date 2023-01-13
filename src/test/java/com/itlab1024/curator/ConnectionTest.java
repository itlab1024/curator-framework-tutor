package com.itlab1024.curator;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.junit.jupiter.api.Test;

public class ConnectionTest {
    /**
     * 创建连接
     */
    @Test
    public void TestConnection1() throws Exception {
        CuratorFramework curatorFramework = CuratorFrameworkFactory.newClient("172.20.98.4:2181",  new ExponentialBackoffRetry(1000,3));
        curatorFramework.start();
        curatorFramework.create().forPath("/test");
    }
}
