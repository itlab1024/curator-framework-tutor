package com.itlab1024.curator;

import org.apache.curator.SessionFailedRetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.RetryForever;
import org.apache.curator.retry.RetryNTimes;
import org.apache.curator.retry.RetryOneTime;
import org.junit.jupiter.api.Test;

import java.util.concurrent.TimeUnit;

public class RetryTest {
    /**
     * RetryForever
     */
    @Test
    public void testRetryForever() throws Exception {
        CuratorFramework curatorFramework = CuratorFrameworkFactory.newClient("unknownHost:2181",  new RetryForever(2000));
        curatorFramework.start();
    }

    /**
     * SessionFailedRetryPolicy
     * @throws Exception
     */
    @Test
    public void testSessionFailedRetryPolicy() throws Exception {
        CuratorFramework curatorFramework = CuratorFrameworkFactory.newClient("localhost:2181", 10000, 10000,  new SessionFailedRetryPolicy(new RetryForever(2000)));
        curatorFramework.start();
        TimeUnit.MINUTES.sleep(1);
    }

    @Test
    public void testRetryNTimes() throws Exception {
        CuratorFramework curatorFramework = CuratorFrameworkFactory.newClient("unknownHost:2181", new RetryNTimes(5, 1000));
        curatorFramework.start();
        TimeUnit.DAYS.sleep(1);
    }

    @Test
    public void testRetryOneTime() throws Exception {
        CuratorFramework curatorFramework = CuratorFrameworkFactory.newClient("unknownHost:2181", new RetryOneTime(1000));
        curatorFramework.start();
        TimeUnit.DAYS.sleep(1);
    }
}
