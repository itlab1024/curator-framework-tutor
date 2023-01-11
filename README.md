# Apache Curator Framework框架学习
Apache Curator 是 Apache ZooKeeper（分布式协调服务）的 Java/JVM 客户端库。它包括一个高级API框架和实用程序，使使用Apache ZooKeeper变得更加容易和可靠。

![](https://itlab1024-1256529903.cos.ap-beijing.myqcloud.com/202301111048449.png)

![](https://itlab1024-1256529903.cos.ap-beijing.myqcloud.com/202301111050442.png)
# 依赖
curator 有很多的依赖，比如如下是maven依赖官方说明
![](https://itlab1024-1256529903.cos.ap-beijing.myqcloud.com/202301111130055.png)
一般情况下只要引入`curator-recipes`基本就够用。他包含了`client`和`framework`的依赖，会自动下载下来。
![](https://itlab1024-1256529903.cos.ap-beijing.myqcloud.com/202301111131555.png)
# 创建项目并引入依赖
pom文件
```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>

    <groupId>com.itlab1024</groupId>
    <artifactId>curator-framework-tutorial</artifactId>
    <version>1.0-SNAPSHOT</version>

    <properties>
        <maven.compiler.source>8</maven.compiler.source>
        <maven.compiler.target>8</maven.compiler.target>
        <project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>
    </properties>
    <dependencies>
        <dependency>
            <groupId>org.apache.curator</groupId>
            <artifactId>curator-recipes</artifactId>
            <version>5.4.0</version>
        </dependency>
        <dependency>
            <groupId>org.slf4j</groupId>
            <artifactId>slf4j-api</artifactId>
            <version>2.0.6</version>
        </dependency>
        <dependency>
            <groupId>org.slf4j</groupId>
            <artifactId>slf4j-simple</artifactId>
            <version>2.0.6</version>
        </dependency>
        <dependency>
            <groupId>org.junit.jupiter</groupId>
            <artifactId>junit-jupiter</artifactId>
            <version>5.9.0</version>
            <scope>test</scope>
        </dependency>
    </dependencies>
</project>
```
# 创建连接
curator主要通过工厂类`CuratorFrameworkFactory`的`newClient`方法创建连接
![](https://itlab1024-1256529903.cos.ap-beijing.myqcloud.com/202301111300623.png)
有三种多态方法。
```text
public static CuratorFramework newClient(String connectString, int sessionTimeoutMs, int connectionTimeoutMs, RetryPolicy retryPolicy, ZKClientConfig zkClientConfig)
public static CuratorFramework newClient(String connectString, int sessionTimeoutMs, int connectionTimeoutMs, RetryPolicy retryPolicy)
public static CuratorFramework newClient(String connectString, RetryPolicy retryPolicy)
```
参数说明：
* connectString：连接字符串，服务器访问地址例如localhost:2181(注意是IP（域名）+ 端口),如果是集群地址，则用逗号(,)隔开即可。
* sessionTimeoutMs：会话超时时间，单位毫秒，如果不设置则先去属性中找`curator-default-session-timeout`的值，如果没设置，则默认是60 * 1000毫秒。
* connectionTimeoutMs：连接超时时间，单位毫秒，如果不设置则先去属性中找`curator-default-connection-timeout`的值，如果没设置，则默认是15 * 1000毫秒。
* RetryPolicy：重试策略，后面具体讲解。

使用Java代码创建连接并创建一个节点
```java
package com.itlab1024.curator.connection;

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
        CuratorFramework curatorFramework = CuratorFrameworkFactory.newClient("172.30.140.89:2181",  new ExponentialBackoffRetry(1000,3));
        curatorFramework.start();
        curatorFramework.create().forPath("/test");
    }
}
```
运行完毕后查看结果：
![](https://itlab1024-1256529903.cos.ap-beijing.myqcloud.com/202301111344287.png)