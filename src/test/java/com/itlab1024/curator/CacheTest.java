package com.itlab1024.curator;

import lombok.extern.slf4j.Slf4j;
import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.listen.Listenable;
import org.apache.curator.framework.recipes.cache.CuratorCache;
import org.apache.curator.framework.recipes.cache.CuratorCacheListener;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

@Slf4j
public class CacheTest {
    String connectString = "172.20.98.4:2181";
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
        CuratorCacheListener curatorCacheListener = CuratorCacheListener.builder().forInitialized(() -> System.out.println("Initialized")) // 当curatorCache.start()执行完毕的时候，执行此方法
                .build();
        curatorCache.listenable().addListener(curatorCacheListener);
        curatorCache.start();
        TimeUnit.MINUTES.sleep(10);
    }

    /**
     * forCreates监听 CuratorCacheListener.Type.NODE_CREATED
     *
     * @throws Exception
     */
    @Test
    public void testCache2() throws Exception {
        CuratorFramework curatorFramework = CuratorFrameworkFactory.newClient(connectString, retryPolicy);
        curatorFramework.start();
        CuratorCache curatorCache = CuratorCache.build(curatorFramework, "/ns1", CuratorCache.Options.SINGLE_NODE_CACHE);
        CuratorCacheListener curatorCacheListener = CuratorCacheListener.builder()
                .forInitialized(() -> {
                    log.info("forInitialized回调");
                    log.debug("--------");
                })

                .forCreates(childData -> {
                    log.debug("forCreates回调执行, path=[{}], data=[{}], stat=[{}]", childData.getPath(), Objects.isNull(childData.getData()) ? null : new String(childData.getData(), StandardCharsets.UTF_8), childData.getStat());
                    log.debug("--------");
                })

                .forNodeCache(() -> {
                    log.debug("forNodeCache回调");
                    log.debug("--------");
                })

                .forChanges((oldNode, node) -> {
                    log.debug("forChanges回调, oldNode.path=[{}], oldNode.data=[{}], oldNode.stat=[{}], node.path=[{}], node.data=[{}], node.stat=[{}]", oldNode.getPath(), Objects.isNull(oldNode.getData()) ? null : new String(oldNode.getData(), StandardCharsets.UTF_8), oldNode.getStat(), node.getPath(), Objects.isNull(node.getData()) ? null : new String(node.getData(), StandardCharsets.UTF_8), node.getStat());
                    log.debug("--------");
                })

                .forDeletes(childData -> {
                    log.debug("forDeletes回调执行, path=[{}], data=[{}], stat=[{}]", childData.getPath(), Objects.isNull(childData.getData()) ? null : new String(childData.getData(), StandardCharsets.UTF_8), childData.getStat());
                    log.debug("--------");
                })

                .forAll((type, oldNode, node) -> {
                    log.debug("forAll回调");
                    log.debug("type=[{}]", type);
                    if (Objects.nonNull(oldNode)) {
                        log.debug("oldNode.path=[{}], oldNode.data=[{}], oldNode.stat=[{}]", oldNode.getPath(), Objects.isNull(oldNode.getData()) ? null : new String(oldNode.getData(), StandardCharsets.UTF_8), oldNode.getStat());
                    }
                    if (Objects.nonNull(node)) {
                        log.debug("node.path=[{}], node.data=[{}], node.stat=[{}]", node.getPath(), Objects.isNull(node.getData()) ? null : new String(node.getData(), StandardCharsets.UTF_8), node.getStat());
                    }
                    log.debug("--------");
                })

                .forCreatesAndChanges((oldNode, node) -> {
                    if (Objects.nonNull(oldNode)) {
                        log.debug("oldNode.path=[{}], oldNode.data=[{}], oldNode.stat=[{}]", oldNode.getPath(), Objects.isNull(oldNode.getData()) ? null : new String(oldNode.getData(), StandardCharsets.UTF_8), oldNode.getStat());
                    }
                    if (Objects.nonNull(node)) {
                        log.debug("node.path=[{}], node.data=[{}], node.stat=[{}]", node.getPath(), Objects.isNull(node.getData()) ? null : new String(node.getData(), StandardCharsets.UTF_8), node.getStat());
                    }
                    log.debug("--------");
                })
                .build();

        CuratorCacheListener c1 = CuratorCacheListener.builder().forTreeCache(curatorFramework, (client, event) -> {
            log.debug("forTreeCache回调, type=[{}], data=[{}]", event.getType(), event.getData());
            log.debug("--------");
        }).build();

        CuratorCacheListener c2 = CuratorCacheListener.builder().forPathChildrenCache("/test", curatorFramework, (client, event) -> {
            log.debug("forPathChildrenCache回调, type=[{}], data=[{}], initialData=[{}]", event.getType(), event.getData(), event.getInitialData());
            log.debug("--------");
        }).build();

        Listenable<CuratorCacheListener> listenable = curatorCache.listenable();
        listenable.addListener(curatorCacheListener);
        listenable.addListener(c1);
        listenable.addListener(c2);
        curatorCache.start();

        // api操作
        curatorFramework.create().creatingParentsIfNeeded().forPath("/ns1");
        curatorFramework.create().creatingParentsIfNeeded().forPath("/ns1/sub1");
        TimeUnit.MINUTES.sleep(10);
        curatorCache.close();
    }
}
