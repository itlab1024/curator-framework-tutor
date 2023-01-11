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

# 创建节点
创建节点使用`create`方法，该方法返回一个`CreateBuilder`他是一个建造者模式的类。用于创建节点。
```java
package com.itlab1024.curator.connection;

import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.junit.jupiter.api.Test;

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
}
```
创建完毕后，通过命令行查看节点：
![](https://itlab1024-1256529903.cos.ap-beijing.myqcloud.com/202301111419073.png)
看到值是`10.112.33.229`,可实际上我并未给节点设置值，这个值是框架默认设置的，客户端的IP。
这个默认值可以修改，此时不能使用`newClient`方法，需要使用工厂的builder自己构建设置。示例代码如下：
```java
@Test
public void testCreateDefaultData() throws Exception {
    CuratorFrameworkFactory.Builder builder = CuratorFrameworkFactory.builder().defaultData("默认值".getBytes(StandardCharsets.UTF_8));
    CuratorFramework client = builder.connectString(connectString).retryPolicy(retryPolicy).build();
    client.start();
    client.create().forPath("/defaultDataTest");
}
```
运行结果：
![](https://itlab1024-1256529903.cos.ap-beijing.myqcloud.com/202301111424095.png)
可以看到，默认值已经被修改为`默认值`。

创建节点时如果节点存在，则会抛出`NodeExistsException`异常
![](https://itlab1024-1256529903.cos.ap-beijing.myqcloud.com/202301111410229.png)

**使用forPath设置节点的值**

forPath还接收第二个参数（节点的值，字节数组类型）
```java
@Test
public void testCreate2() throws Exception {
    CuratorFramework curatorFramework = CuratorFrameworkFactory.newClient(connectString, retryPolicy);
    curatorFramework.start();
    curatorFramework.create().forPath("/test2", "用户自己设置的值".getBytes(StandardCharsets.UTF_8));
}
```
运行结果：
![](https://itlab1024-1256529903.cos.ap-beijing.myqcloud.com/202301111427929.png)
可见正确设置了值。

**节点模式设置**

可以通过`withMode`方法设置节点的类型，未显示指定的节点都是持久性节点。

```java
/**
 * 设置节点类型
 * @throws Exception
 */
@Test
public void testCreate3() throws Exception {
    CuratorFramework curatorFramework = CuratorFrameworkFactory.newClient(connectString, retryPolicy);
    curatorFramework.start();
    curatorFramework.create().withMode(CreateMode.EPHEMERAL).forPath("/EPHEMERAL1");
    // 临时节点，会话结束就会删除，线程睡眠用于延长会话时间
    TimeUnit.SECONDS.sleep(30);
}
```
查看结果：
![](https://itlab1024-1256529903.cos.ap-beijing.myqcloud.com/202301111512167.png)
可以看到临时节点，红色框内只有临时节点该属性才是非零。

**TTL时长设置**

使用`withTtl`设置时长，单位毫秒。当模式为 CreateMode.PERSISTENT_WITH_TTL 或CreateMode.PERSISTENT_SEQUENTIAL_WITH_TTL时指定 TTL。必须大于 0 且小于或等于 EphemeralType.MAX_TTL。
