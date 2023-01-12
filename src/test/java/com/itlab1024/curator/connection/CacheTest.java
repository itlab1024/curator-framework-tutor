package com.itlab1024.curator.connection;

import lombok.extern.slf4j.Slf4j;
import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.cache.CuratorCache;
import org.apache.curator.framework.recipes.cache.CuratorCacheListener;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

@Slf4j
public class CacheTest {
    String connectString = "172.30.140.89:2181";
    RetryPolicy retryPolicy = new ExponentialBackoffRetry(1000, 3);

    /**
     * 监听（初始化）
     *
     * @throws Exception
     */
    @Test
    public void testCache1() throws Exception {
        CuratorFramework curatorFramework = CuratorFrameworkFactory.newClient(connectString, retryPolicy);
        curatorFramework.start();

        CuratorCache curatorCache = CuratorCache.build(curatorFramework, "/test");
        CuratorCacheListener curatorCacheListener = CuratorCacheListener.builder()
                .forInitialized(() -> System.out.println("Initialized")) // 当curatorCache.start()执行完毕的时候，执行此方法
                .build();
        curatorCache.listenable().addListener(curatorCacheListener);
        curatorCache.start();
        TimeUnit.MINUTES.sleep(10);
    }

    @Test
    public void testCache2() throws Exception {
        CuratorFramework curatorFramework = CuratorFrameworkFactory.newClient(connectString, retryPolicy);
        curatorFramework.start();

        CuratorCache curatorCache = CuratorCache.build(curatorFramework, "/test");
        CuratorCacheListener curatorCacheListener = CuratorCacheListener.builder()
                .forCreates(childData -> {
                    log.debug("forCreates回调执行, path=[{}], data=[{}], stat=[{}]", childData.getPath()
                            , Objects.isNull(childData.getData()) ? null : new String(childData.getData(), StandardCharsets.UTF_8), childData.getStat());
                })
                .build();
        curatorCache.listenable().addListener(curatorCacheListener);
        curatorCache.start();
        TimeUnit.MINUTES.sleep(10);
    }
}
