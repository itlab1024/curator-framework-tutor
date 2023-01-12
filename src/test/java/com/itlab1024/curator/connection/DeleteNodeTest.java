package com.itlab1024.curator.connection;

import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.junit.jupiter.api.Test;

import java.util.List;

public class DeleteNodeTest {
    String connectString = "172.30.140.89:2181";
    RetryPolicy retryPolicy = new ExponentialBackoffRetry(1000, 3);

    /**
     * 删除节点
     * @throws Exception
     */
    @Test
    public void testDelete1() throws Exception {
        CuratorFramework curatorFramework = CuratorFrameworkFactory.newClient(connectString, retryPolicy);
        curatorFramework.start();
        curatorFramework.delete().forPath("/namespace1");
    }

    /**
     * 删除节点（包含子节点）
     * @throws Exception
     */
    @Test
    public void testDelete2() throws Exception {
        CuratorFramework curatorFramework = CuratorFrameworkFactory.newClient(connectString, retryPolicy);
        curatorFramework.start();
        curatorFramework.delete().deletingChildrenIfNeeded().forPath("/namespace1");
    }
}
