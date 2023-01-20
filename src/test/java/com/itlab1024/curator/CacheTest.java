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
    String connectString = "localhost:2181";
    RetryPolicy retryPolicy = new ExponentialBackoffRetry(1000, 3);


    /**
     *
     * @throws Exception
     */
    @Test
    public void testCache1() throws Exception {
        CuratorFramework curatorFramework = CuratorFrameworkFactory.newClient(connectString, retryPolicy);
        curatorFramework.start();
        CuratorCache curatorCache = CuratorCache.builder(curatorFramework, "/ns1").build();
        CuratorCacheListener curatorCacheListener = CuratorCacheListener.builder()
                .forInitialized(() -> {
                    log.info("forInitialized回调");
                    log.info("--------");
                })

                .forCreates(childData -> {
                    log.info("forCreates回调执行, path=[{}], data=[{}], stat=[{}]", childData.getPath(), Objects.isNull(childData.getData()) ? null : new String(childData.getData(), StandardCharsets.UTF_8), childData.getStat());
                    log.info("--------");
                })

                .forNodeCache(() -> {
                    log.info("forNodeCache回调");
                    log.info("--------");
                })

                .forChanges((oldNode, node) -> {
                    log.info("forChanges回调, oldNode.path=[{}], oldNode.data=[{}], oldNode.stat=[{}], node.path=[{}], node.data=[{}], node.stat=[{}]", oldNode.getPath(), Objects.isNull(oldNode.getData()) ? null : new String(oldNode.getData(), StandardCharsets.UTF_8), oldNode.getStat(), node.getPath(), Objects.isNull(node.getData()) ? null : new String(node.getData(), StandardCharsets.UTF_8), node.getStat());
                    log.info("--------");
                })

                .forDeletes(childData -> {
                    log.info("forDeletes回调执行, path=[{}], data=[{}], stat=[{}]", childData.getPath(), Objects.isNull(childData.getData()) ? null : new String(childData.getData(), StandardCharsets.UTF_8), childData.getStat());
                    log.info("--------");
                })

                .forAll((type, oldNode, node) -> {
                    log.info("forAll回调");
                    log.info("type=[{}]", type);
                    if (Objects.nonNull(oldNode)) {
                        log.info("oldNode.path=[{}], oldNode.data=[{}], oldNode.stat=[{}]", oldNode.getPath(), Objects.isNull(oldNode.getData()) ? null : new String(oldNode.getData(), StandardCharsets.UTF_8), oldNode.getStat());
                    }
                    if (Objects.nonNull(node)) {
                        log.info("node.path=[{}], node.data=[{}], node.stat=[{}]", node.getPath(), Objects.isNull(node.getData()) ? null : new String(node.getData(), StandardCharsets.UTF_8), node.getStat());
                    }
                    log.info("--------");
                })

                .forCreatesAndChanges((oldNode, node) -> {
                    log.info("forCreatesAndChanges回调");
                    if (Objects.nonNull(oldNode)) {
                        log.info("oldNode.path=[{}], oldNode.data=[{}], oldNode.stat=[{}]", oldNode.getPath(), Objects.isNull(oldNode.getData()) ? null : new String(oldNode.getData(), StandardCharsets.UTF_8), oldNode.getStat());
                    }
                    if (Objects.nonNull(node)) {
                        log.info("node.path=[{}], node.data=[{}], node.stat=[{}]", node.getPath(), Objects.isNull(node.getData()) ? null : new String(node.getData(), StandardCharsets.UTF_8), node.getStat());
                    }
                    log.info("--------");
                })
                .build();
        // 获取监听器列表容器
        Listenable<CuratorCacheListener> listenable = curatorCache.listenable();
        // 将监听器放入容器中
        listenable.addListener(curatorCacheListener);
        // curatorCache必须启动
        curatorCache.start();
        // 延时，以保证连接不关闭
        TimeUnit.DAYS.sleep(10);
        curatorCache.close();
    }
}
