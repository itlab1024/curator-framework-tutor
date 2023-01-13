# Apache Curator Framework框架学习
Apache Curator 是 Apache ZooKeeper（分布式协调服务）的 Java/JVM 客户端库。它包括一个高级API框架和实用程序，使使用Apache ZooKeeper变得更加容易和可靠。

![](https://itlab1024-1256529903.cos.ap-beijing.myqcloud.com/202301131005419.png)

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
            <groupId>org.projectlombok</groupId>
            <artifactId>lombok</artifactId>
            <version>1.18.24</version>
            <scope>test</scope>
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



# **名称空间（Namespace）**

curator中名称空间的含义，就是设置一个公共的父级path，之后的操作全部都是基于该path。

```java
/**
 * 名称空间
 * @throws Exception
 */
@Test
public void testCreate6() throws Exception {
    CuratorFramework curatorFramework = CuratorFrameworkFactory.newClient(connectString, retryPolicy);
    curatorFramework.start();
    CuratorFramework c2 = curatorFramework.usingNamespace("namespace1");
    c2.create().forPath("/node1");
    c2.create().forPath("/node2");
}
```

查看运行结果：
![](https://itlab1024-1256529903.cos.ap-beijing.myqcloud.com/202301121316314.png)



# CRUD基础

## 创建节点

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

### **使用forPath设置节点的值**

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

### **节点模式设置**

可以通过`withMode`方法设置节点的类型，为显示指定的节点都是持久性节点。

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

### **TTL时长设置**

使用`withTtl`设置时长，单位毫秒。当模式为 CreateMode.PERSISTENT_WITH_TTL 或CreateMode.PERSISTENT_SEQUENTIAL_WITH_TTL时指定 TTL。必须大于 0 且小于或等于 EphemeralType.MAX_TTL。
```java
/**
 * 测试ttl
 * @throws Exception
 */
@Test
public void testCreate5() throws Exception {
    CuratorFramework curatorFramework = CuratorFrameworkFactory.newClient(connectString, retryPolicy);
    curatorFramework.start();
    curatorFramework.create().withTtl(10000).withMode(CreateMode.PERSISTENT_WITH_TTL).forPath("/ttl1");
}
```
可能出现如下错误：
![](https://itlab1024-1256529903.cos.ap-beijing.myqcloud.com/202301121306568.png)
这是因为TTL默认是关闭的，需要打开（zoo.cfg中设置`extendedTypesEnabled=true`）。
再次运行：
```shell
[zk: localhost:2181(CONNECTED) 8] ls /
[defaultDataTest, hiveserver2_zk, test, test2, ttl1, zookeeper]
#等待10秒后再次查看，ttl1节点自动被删除。
[zk: localhost:2181(CONNECTED) 9] ls /
[defaultDataTest, hiveserver2_zk, test, test2, zookeeper]
```
### ACL权限

创建节点时设置ACL，主要通过`withACL`方法设置，接收一个`List<ACL>`类型的参数。

`ACL`实例对象，通过该类的构造方法创建，类似`ACL acl = new ACL(ZooDefs.Perms.ALL, ZooDefs.Ids.ANYONE_ID_UNSAFE);`

```java
/**
 * 测试acl
 * @throws Exception
 */
@Test
public void testCreate7() throws Exception {
    CuratorFramework curatorFramework = CuratorFrameworkFactory.newClient(connectString, retryPolicy);
    curatorFramework.start();
    List<ACL> aclList = new ArrayList<>();
    ACL acl = new ACL(ZooDefs.Perms.ALL, ZooDefs.Ids.ANYONE_ID_UNSAFE);
    aclList.add(acl);
    curatorFramework.create().withACL(aclList).forPath("/acl1");
}
```

运行结果：

![image-20230112153655747](https://itlab1024-1256529903.cos.ap-beijing.myqcloud.com/202301121536793.png)

运行完毕后，通过命令行查看权限，可以看到已经设置成功。

如果不设置ACL，默认则是`new ACL(Perms.ALL, ANYONE_ID_UNSAFE)`。

## 查询值

查询数据使用`getData`方法。

```java
/**
 * 查询节点的值
 * @throws Exception
 */
@Test
public void testGetData() throws Exception {
    CuratorFramework curatorFramework = CuratorFrameworkFactory.newClient(connectString, retryPolicy);
    curatorFramework.start();
    byte[] bytes = curatorFramework.getData().forPath("/test");
    System.out.println("/test节点的值是:" + new String(bytes, StandardCharsets.UTF_8));
}
```

结果：

![命令行查询](https://itlab1024-1256529903.cos.ap-beijing.myqcloud.com/202301121347952.png)



![api查询结果](https://itlab1024-1256529903.cos.ap-beijing.myqcloud.com/202301121347616.png)

## 设置值

使用`setData`，配合`forpath`方法。

```java
/**
 * 设置节点的值
 * @throws Exception
 */
@Test
public void testGetData2() throws Exception {
    CuratorFramework curatorFramework = CuratorFrameworkFactory.newClient(connectString, retryPolicy);
    curatorFramework.start();
    byte[] bytes = curatorFramework.getData().forPath("/test");
    System.out.println("/test节点的原始值是:" + new String(bytes, StandardCharsets.UTF_8));
    curatorFramework.setData().forPath("/test", "updated".getBytes(StandardCharsets.UTF_8));
    bytes = curatorFramework.getData().forPath("/test");
    System.out.println("/test节点的新值是:" + new String(bytes, StandardCharsets.UTF_8));
}
```

运行结果是：

![image-20230112150323445](https://itlab1024-1256529903.cos.ap-beijing.myqcloud.com/202301121503512.png)

## 获取孩子节点

```java
/**
 * 获取孩子节点
 * @throws Exception
 */
@Test
public void testGetState() throws Exception {
    CuratorFramework curatorFramework = CuratorFrameworkFactory.newClient(connectString, retryPolicy);
    curatorFramework.start();
    List<String> children = curatorFramework.getChildren().forPath("/namespace1");
    children.forEach(System.out::println);
}
```

运行结果：

![image-20230112152418448](https://itlab1024-1256529903.cos.ap-beijing.myqcloud.com/202301121524560.png)

![image-20230112152442481](https://itlab1024-1256529903.cos.ap-beijing.myqcloud.com/202301121524557.png)

## 获取ACL

```java
package com.itlab1024.curator.connection;

import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.data.ACL;
import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class ACLTest {
    String connectString = "172.30.140.89:2181";
    RetryPolicy retryPolicy = new ExponentialBackoffRetry(1000, 3);

    /**
     * 获取Acl列表
     */
    @Test
    public void testAcl1() throws Exception {
        CuratorFramework curatorFramework = CuratorFrameworkFactory.newClient(connectString, retryPolicy);
        curatorFramework.start();
        List<ACL> acls = curatorFramework.getACL().forPath("/test");
        acls.forEach(acl -> System.out.println(acl.getId() + " " + acl.getPerms()));
    }

}
```

运行结果：

![image-20230112153012559](https://itlab1024-1256529903.cos.ap-beijing.myqcloud.com/202301121530612.png)

## 删除节点

使用`delete`，搭配`forPath`方法，删除指定的节点。

```java
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
        curatorFramework.delete().forPath("/test");
    }
}
```

程序执行完毕后，通过命令行查询`/test`可知已经被删除。

![image-20230112154133228](https://itlab1024-1256529903.cos.ap-beijing.myqcloud.com/202301121541279.png)

如果被删除的节点有孩子节点，则无法删除，抛出`NotEmptyException`。



**那么如何删除包含子节点的节点呢？需要使用`deletingChildrenIfNeeded`方法**

```java
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
```

运行后，查看该节点



![image-20230112182019037](https://itlab1024-1256529903.cos.ap-beijing.myqcloud.com/202301121820090.png)



节点已经被删除。并且级联删除了子节点。



## 检查节点是否存在

使用`checkExists()`搭配`forPath`来实现，返回一个`Stat`对象信息。

```
package com.itlab1024.curator.connection;

import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.zookeeper.data.Stat;
import org.junit.jupiter.api.Test;

public class CheckExistsTest {
    String connectString = "172.30.140.89:2181";
    RetryPolicy retryPolicy = new ExponentialBackoffRetry(1000, 3);

    /**
     * 检查是否存在
     * @throws Exception
     */
    @Test
    public void testGetState() throws Exception {
        CuratorFramework curatorFramework = CuratorFrameworkFactory.newClient(connectString, retryPolicy);
        curatorFramework.start();
        Stat stat = curatorFramework.checkExists().forPath("/namespace1");
        System.out.println(stat);
    }
}
```

运行结果：

![image-20230112173212601](https://itlab1024-1256529903.cos.ap-beijing.myqcloud.com/202301121732695.png)



`stat`的具体信息如下：



![image-20230112173310084](https://itlab1024-1256529903.cos.ap-beijing.myqcloud.com/202301121733222.png)

## 查看会话状态

使用`getState()`。

```java
package com.itlab1024.curator.connection;

import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.imps.CuratorFrameworkState;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.concurrent.TimeUnit;

public class GetStateTest {
    String connectString = "172.30.140.89:2181";
    RetryPolicy retryPolicy = new ExponentialBackoffRetry(1000, 3);

    /**
     * 查询客户端状态
     * @throws Exception
     */
    @Test
    public void testGetState() throws Exception {
        CuratorFramework curatorFramework = CuratorFrameworkFactory.newClient(connectString, retryPolicy);
        CuratorFrameworkState state = curatorFramework.getState();
        System.out.println("状态是" + state); // 状态是LATENT
        curatorFramework.start();
        state = curatorFramework.getState();
        System.out.println("状态是" + state); // 状态是STARTED
        curatorFramework.close();
        state = curatorFramework.getState();
        System.out.println("状态是" + state); // 状态是STOPPED
    }
}
```

# 事务



```java
package com.itlab1024.curator.connection;

import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.api.GetConfigBuilder;
import org.apache.curator.framework.api.transaction.CuratorMultiTransaction;
import org.apache.curator.framework.api.transaction.CuratorOp;
import org.apache.curator.framework.api.transaction.CuratorTransaction;
import org.apache.curator.framework.api.transaction.CuratorTransactionResult;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.zookeeper.data.Stat;
import org.apache.zookeeper.server.quorum.flexible.QuorumVerifier;
import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.List;

public class TransactionTest{
    String connectString = "172.30.140.89:2181";
    RetryPolicy retryPolicy = new ExponentialBackoffRetry(1000, 3);

    /**
     * 查询客户端状态
     * @throws Exception
     */
    @Test
    public void testTransaction() throws Exception {
        CuratorFramework curatorFramework = CuratorFrameworkFactory.newClient(connectString, retryPolicy);
        curatorFramework.start();
        CuratorOp createOp = curatorFramework.transactionOp().create().forPath("/transaction1");
        CuratorOp setDataOp = curatorFramework.transactionOp().setData().forPath("/transaction2", "transaction2".getBytes(StandardCharsets.UTF_8));
        CuratorOp deleteOp = curatorFramework.transactionOp().delete().forPath("/transaction3");

        List<CuratorTransactionResult> result = curatorFramework.transaction().forOperations(createOp, setDataOp, deleteOp);
        result.forEach(rt -> System.out.println(rt.getForPath() + "---" + rt.getType()));
    }
}
```

运行程序前先看下zk节点情况

![image-20230112175631300](https://itlab1024-1256529903.cos.ap-beijing.myqcloud.com/202301121756369.png)

可以看到没有`transaction1`和`transaction2`和`transaction3`。

运行程序会出现如下异常。

![image-20230112175722403](https://itlab1024-1256529903.cos.ap-beijing.myqcloud.com/202301121757512.png)



出现异常则事务应该回滚，也就是说`transaction1`节点不应该创建成功。

![image-20230112175815921](https://itlab1024-1256529903.cos.ap-beijing.myqcloud.com/202301121758968.png)

通过上图可知确实没有创建成功。

接下来我通过命令长创建`/transaction2`和`/transaction3`这两个节点。

![image-20230112180734954](https://itlab1024-1256529903.cos.ap-beijing.myqcloud.com/202301121807011.png)

创建完毕，并且可以看到`/transaction2`节点的值是`null`。

重新运行程序后，不会发生异常。



![image-20230112180839996](https://itlab1024-1256529903.cos.ap-beijing.myqcloud.com/202301121808093.png)



通过命令行看下事务是否完全执行成功。

![image-20230112180946262](https://itlab1024-1256529903.cos.ap-beijing.myqcloud.com/202301121809319.png)

可以看到`/transaction1`节点创建成功，`/transaction2`节点的值修改成功。`/transaction3`节点被删除。说明事务是有效的！



---

为了演示清晰，我先清理掉所有节点。

![image-20230112195536876](https://itlab1024-1256529903.cos.ap-beijing.myqcloud.com/202301121955942.png)



# 监听节点

本版本中`PathChildrenCache`、`PathChildrenCacheMod`、`TreeCache`都已经是过期的了，官方推荐使用`CuratorCache`。

```java
package com.itlab1024.curator.connection;

import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.api.CuratorEvent;
import org.apache.curator.framework.api.CuratorListener;
import org.apache.curator.framework.recipes.cache.*;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.zookeeper.data.Stat;
import org.junit.jupiter.api.Test;

import java.util.concurrent.TimeUnit;

public class CacheTest {
    String connectString = "172.30.140.89:2181";
    RetryPolicy retryPolicy = new ExponentialBackoffRetry(1000, 3);

    /**
     * 监听（初始化）
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
}
```

上面的代码就是创建监听节点的核心代码。

> 以前的监听类型是不同的类（过期的类）实现的。现在是通过不同的forXXX方法指定的（例如：`forInitialized`），接下来一一讲解并实战观测结果。

## forInitialized

初始化完成的时候调用，也就是`curatorCache.start()`完成时候。

上面的代码就是例子，运行结果如下：

![image-20230112194847299](https://itlab1024-1256529903.cos.ap-beijing.myqcloud.com/202301121948389.png)



## forCreates

节点创建监听器，监听类型是`CuratorCacheListener.Type.NODE_CREATED`

