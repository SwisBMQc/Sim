# Sim
一个简单的im聊天系统

- sim-android
- sim-server

这里android前端和后端都使用的是netty，就没有做分布式了。



## sim-android-dome

这个简单的实现了一个控制台的dome，记录一下学习过程



>  参考文章和代码（代码详情请看这个）：
>
> [跟着源码学IM(二)：自已开发IM很难？手把手教你撸一个Andriod版IM-IM开发/专项技术区 - 即时通讯开发者社区!](http://www.52im.net/thread-2671-1-1.html)

基于TCP和Protobuf作为通信协议，使用netty构建客户端服务 

Protocol Buffers 是一种轻便高效的结构化数据存储格式，用于数据的序列化，很快，很小，比json更牛，深受广大消费者喜爱

记录一下自己的学习过程

 效果如图

<img src="https://img-blog.csdnimg.cn/c6565a6a052f4ff48abe3419ad6168e6.png" alt="img" style="zoom:67%;" />

 项目结构如图，关键在netty包

<img src="https://img-blog.csdnimg.cn/68e77329d7f74677b980c2b06b55486f.png" alt="img" style="zoom:67%;" />



### 1. 准备工作

创建项目，添加依赖

创建新项目，添加新module(Android Library)，设置名称 im_lib 目录如图

<img src="https://img-blog.csdnimg.cn/d2b2f473a37f4e0f968fe464158ac3e3.png" alt="img" style="zoom:67%;" />

分析消息结构，编写.proto文件，编译 .proto 文件之前需要下载protobuf 编译器，配置环境变量

```bash
syntax = "proto3";// 指定protobuf版本
option java_package = "com.sy.im.protobuf";// 指定包名
option java_outer_classname = "MessageProtobuf";// 指定生成的类名

message Msg {
    Head head = 1;// 消息头
    string body = 2;// 消息体
}

message Head {
    string msgId = 1;// 消息id
    int32 msgType = 2;// 消息类型
    int32 msgContentType = 3;// 消息内容类型
    string fromId = 4;// 消息发送者id
    string toId = 5;// 消息接收者id
    int64 timestamp = 6;// 消息时间戳
    int32 statusReport = 7;// 状态报告
    string extend = 8;// 扩展字段，以key/value形式存放的json
}
```

这里msg.proto文件是在com目录下的，右键-Open In-Terminal，输入指令，执行生成java文件

```bash
protoc --java_out=../ msg.proto
```


im_ilb/build.gradle，引入依赖

```Groovy
compileOnly "com.alibaba:fastjson:1.2.49"
api "com.google.protobuf:protobuf-java:3.24.4"
compileOnly files('libs/netty-tcp-4.1.33-1.0.jar')
```


把 netty-tcp-4.1.33-1.0.jar添加到im_lib/libs目录下

 ![img](https://img-blog.csdnimg.cn/4e75661e87c340f3bb0afcaca83db0db.png)

gradle sync，准备工作结束，开始业务逻辑的编写



### 2. 接口，封装

编写接口是为了把业务抽象出来，对代码进行解耦

![img](https://img-blog.csdnimg.cn/71ba62b8bd4b4d3eb8fa1912868a8b5e.png)

对 im 服务的客户端进行分析有

1. 初始化方法
2. 建立和关闭连接
3. 发送消息

新建 com/sy/im/interf/IMSClient.java（详情请看源码）

```java
public interface IMSClient {

    /**
     * ims 初始化
     * @param serverUrlList 服务器地址列表
     * @param listener      事件监听器
     * @param callback      ims连接状态回调
     */
    void init(CopyOnWriteArrayList serverUrlList,OnEventListener listener, IMSConnectStatusCallback callback);

    void resetConnect(); // 重置连接

    void resetConnect(boolean isFirst); // 是否第一次连接

    void close(); // 关闭连接，释放资源

    boolean isClosed(); // ims状态

    void sendMsg(MessageProtobuf.Msg msg);// 发送消息

    void sendMsg(MessageProtobuf.Msg msg, boolean isJoinTimeoutManager);// 是否添加超时管理器

}
```

com/sy/im/interf/IMSConnectStatusCallback.java 连接回调
com/sy/im/interf/OnEventListener.java 事件监听器

![img](https://img-blog.csdnimg.cn/0d0e140446bf4139ab11ee0e2c48046c.png)

编写实现类com/sy/im/netty/NettyTcpClient.java 实现接口ISMClient
编写单例模式

```java
    private static volatile  NettyTcpClient instance;

    private NettyTcpClient() {}

    public static NettyTcpClient getInstance(){
        if(null == instance){
            synchronized (NettyTcpClient.class) {
                if (null == instance) {
                    instance = new NettyTcpClient();
                }
            }
        }
        return instance;
    }
```


创建工厂方法

```java
public class IMSClientFactory {
    public static IMSClient getIMSClient(){
        return NettyTcpClient.getInstance();
    }
}
```


### 3. 实现部分

#### 初始化

NettyTcpClient

<img src="https://img-blog.csdnimg.cn/7f65767011fc4f469da4a1aef516be75.png" alt="img" style="zoom:67%;" />

 <img src="https://img-blog.csdnimg.cn/ac44e45139c742f1b5d3f62fd82009b4.png" alt="img" style="zoom:80%;" />

添加 MsgDispatcher，ExecutorServiceFactory
 MsgDispatcher，消息转发器，接收消息并通过OnEventListener转发消息到应用层
 ExecutorServiceFactory，线程池工厂，负责调度重连及心跳线程

#### 连接与重连

重写**resetConnect**方法，把注意力放到 **resetConnect(boolean isFirst)**上

非首次连接，需要在连接之前多等一会，可能因为当前网络不好
 连接时四步如图，双层判断加锁进行并发处理

![img](https://img-blog.csdnimg.cn/e72faca001d94f04a369481c71f81841.png)
 新添加变量
![img](https://img-blog.csdnimg.cn/e35a6fb0234c42c0975286667625f736.png)

私有方法onConnectStatusCallback
根据传入的connectStatus，回调callback中的方法
 ![img](https://img-blog.csdnimg.cn/0e9c9b79c20e41d89e3678ba7853bad3.png)

closeChannel
![img](https://img-blog.csdnimg.cn/bdba1820c0244c8f9ec0e1a0c6ecb0ca.png)

私有内部类 **ResetConnectRunnable**，执行连接任务

 ![img](https://img-blog.csdnimg.cn/90e532ad7ef9498dbc906ffc2b40d8d1.png)

 reConnect() — 初始化bootstrap
 连接服务器 connectServer(),获得server host和port
 调用toServer()：
 ![img](https://img-blog.csdnimg.cn/eff74711fa9e41e9aa3bfc64e2aa1371.png)



#### Bootstrap

![img](https://img-blog.csdnimg.cn/0ca6283511284b4182044e72c2e74e22.png)

TCPChannelInitializerHandler：
 设置了自定义长度解码器，解决TCP拆包粘报问题
 ![img](https://img-blog.csdnimg.cn/45d310667e6043858b7be04a036251a8.png)



#### 握手消息认证处理

当客户端与服务端长连接建立成功后，客户端主动向服务端发送一条登录认证消息，服务端返回认证结果

客户端发送的认证（握手）消息需要从应用层获取——在 IMSClient，OnEventListener 添加方法getHandshakeMsg()

在connectServer()中channel ！= null来判断是否连接成功

 ![img](https://img-blog.csdnimg.cn/be2518527d7b489180f7f03164352717.png)

连接成功后立刻发送握手消息

 ![img](https://img-blog.csdnimg.cn/74066d3792f644bda41ba2eb47c89ea1.png)

LoginAuthRespHandler：

接收服务器响应的消息，拉取应用端的握手消息，比较消息类型，相同则查看响应消息中的status是否握手成功

![img](https://img-blog.csdnimg.cn/0f666f3d8114465788041c6e04fb011b.png)

握手成功后立刻发送心跳消息，添加心跳机制管理

![img](https://img-blog.csdnimg.cn/85f50f71c87e4ed0a43039a14c68b49e.png)

#### 消息超时管理

在将心跳之前，先讲一下这个超时重发，类似小型离线消息

MsgTimeoutTimerManager管理MsgTimeoutTimer，添加，移除和重发

![img](https://img-blog.csdnimg.cn/2bae0c2dab8046e9ae61e596d2efc42f.png)

 MsgTimeoutTimer 一个消息对应一个计时器，对应着一个定时任务

 ![img](https://img-blog.csdnimg.cn/d54181a56a9343ddba387dd20f641e02.png)

注意初始化

```Groovy
msgTimeoutTimerManager = new MsgTimeoutTimerManager(this);
```


#### 心跳机制与读写超时

```
// 添加心跳消息管理
imsClient.addHeartbeatHandler();
```

![img](https://img-blog.csdnimg.cn/1162379cfbba4f44a89da9a84e084302.png)

```
IdleStateHandler -> HeartbeatHandler -> TCPReadHandler
HeartbeatHandler

HeartbeatRespHandler 接收心跳响应消息，打印日志
```

这里心跳部分就结束了，TCPReadHandler 就是消息处理器

当连接失效，出现异常时，触发重连，主要看channelRead

 ![img](https://img-blog.csdnimg.cn/3f6a7b465d4643b89cd4feb4c06ef4d6.png)

sendMsg

![img](https://img-blog.csdnimg.cn/6a30766945cb4849861e5ec228e2f840.png)

### 4. 运行调试和总结

先只测试握手消息，服务端
 绑定好端口，拆包粘包处理，编解码器
 收到消息，判断消息类型，修改extend字段，返回状态

![img](https://img-blog.csdnimg.cn/c898758d703141c2a8212622d4402e95.png)

返回的消息现在LoginAuthRespHandler里进行处理



客户端

 **IMSClientBootstrap**：将数据准备好后，主要是对IMSClient对象的初始化

![img](https://img-blog.csdnimg.cn/747e730613e641f79759ff315c40a676.png)

下面针对初始化参数，创建接口的实现类

```java
IMSEventListener implements OnEventListener
// 注意，这个类主要负责与应用层交互的
```

IMSEventListener 

![img](https://img-blog.csdnimg.cn/5ca956c1757545149e1ab4990f454ce0.png)

服务器收到的

![img](https://img-blog.csdnimg.cn/3bed99cd7f304d9b8593ce132299d337.png)

下面这两个方法先顶替一下

![img](https://img-blog.csdnimg.cn/0d51647f1b5244608e9d0389c89a369f.png)

IMSConnectStatusListener 直接实现 IMSConnectStatusCallback 接口

**值得注意的点：**


> 调试过程中出现Java版本问题，这里使用gradle7.4，统一Java11
>
> 然后是**不能运行main函数的问题**修改一下配置，[AndroidStudio执行main方法报错](https://blog.csdn.net/qq_40307919/article/details/114277740)，可以运行测试



下面捋一下刚创建的客户端：

创建IMSClient实例（实现类是NettyTcpClient）

![img](https://img-blog.csdnimg.cn/9d2ef6e7de344951b6f7f8ecb832b2f2.png)

首先初始化连接，如果channel创建成功，那么连接成功，onConnectStatusCallback立刻发送一条握手消息。

进入TCPChannelInitializerHandler，这三个处理器已经创建完成。收到服务端返回的消息后才创建HeartbeatHandler，即握手成功后才添加心跳管理

![img](https://img-blog.csdnimg.cn/c4903bd770e346cca187880a8b2ebc18.png)

![img](https://img-blog.csdnimg.cn/a00cf84c9c7443cb81310224b546a4af.png)

## sim-android

以上面的dome为基础



### 1. 准备工作



> **Gradle的依赖配置选项**
>
> compileOnly ：依赖项仅在编译时可用，而在运行时不会包含在最终的构建产物中。通常，`compileOnly`用于声明项目对某个API的编译时依赖，而不需要将其打包到最终的构建产物中，**因为它在目标环境中已经存在**。
>
> api：依赖项在编译和运行时都可用，并会包含在最终的构建产物中
>
> implementation：与`api`配置类似，依赖在编译和运行时都可用。与`api`不同的是，`implementation`配置在构建缓存和增量编译方面提供了更好的性能



在 im_lib/build.gradle 中添加
```gr
implementation fileTree(include: ['*.jar'], dir: 'libs')
```

<img src="https://swiimage.oss-cn-guangzhou.aliyuncs.com/img/202310191457508.png" alt="image-20231019145740144" style="zoom:80%;" />

app/build.gradle 中添加

```gr
    implementation project(':im_lib')
    implementation fileTree(dir: 'libs', include: ['*.jar'])
    api "com.google.protobuf:protobuf-java:3.24.4"
    implementation "com.alibaba:fastjson:1.2.49"
```

直接编译不了查看是否修改./idea/gradle.xml（之前提到过）

```xml
<option name="delegatedBuild" value="false" />
```

AndroidManifest.xml：

```xml
<uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />
<uses-permission android:name="android.permission.INTERNET" />
```

简单编写登录页面，测试运行

登录逻辑：

```kotlin
fun performLogin(userId: String, password: String) {
    // 在此处执行登录逻辑
    Log.i("sim","username: $userId, password: $password")

    val token = "token_$userId";
    val hosts = "[{\"host\":\"192.168.200.1\", \"port\":9000}]"; // 注意不要写127.0.0.1
    IMSClientBootstrap.getInstance().init(userId,token,hosts,APP_STATUS_FOREGROUND) //应用在前台标识为0

}
```

<img src="https://swiimage.oss-cn-guangzhou.aliyuncs.com/img/202310191958805.png" alt="客户端连接成功" style="zoom:70%;" />



kotlin相关知识

<img src="https://swiimage.oss-cn-guangzhou.aliyuncs.com/img/202311061304429.png" alt="image-20231106130446354" style="zoom:80%;" />

**view model**

作为ui和应用的中间层，处理逻辑与界面的交互

使用`viewModels`委托属性的好处是，它会自动处理ViewModel的创建和销毁，并且确保在配置更改（如屏幕旋转）时保持ViewModel的数据。此外，它还提供了与ViewModel的生命周期绑定的观察者模式，以便在ViewModel中的数据发生变化时能够及时通知UI层。

总的来说，`private val mainViewModel by viewModels<MainViewModel>()`用于在Android中创建和管理`MainViewModel`实例，并与UI层的生命周期绑定，以便在数据发生变化时更新UI。在这种情况下，它可能用于管理服务器连接状态的逻辑和数据。

**协程**

```kotlin
private val _serverConnectState = MutableStateFlow(value = ServerState.ConnectSuccess)
val serverConnectState: SharedFlow<ServerState> = _serverConnectState
```

1. 下划线开头的变量名可以让编译器更容易地识别出这些局部变量
2. `SharedFlow`共享流是一种特殊类型的流，可以被多个观察者订阅，而不会重复发出相同的值
3. 通常用于Kotlin协程和响应式编程中，用于管理服务器连接状态

<img src="https://swiimage.oss-cn-guangzhou.aliyuncs.com/img/202311061943331.png" alt="image-20231106194308137" style="zoom:80%;" />

1. `SupervisorJob()`创建了一个`SupervisorJob`对象，它是一个特殊的`Job`实现，用于管理协程的层次结构。当一个子协程失败时，`SupervisorJob`不会取消其他子协程，而是只取消失败的子协程。
2. `Dispatchers.Main.immediate`指定了协程的调度器。`Dispatchers.Main`是Kotlin协程库提供的一个调度器，用于在Android主线程上执行协程代码。而`Dispatchers.Main.immediate`则表示在主线程上立即执行协程，不会切换到其他线程。
3. 通过`+`操作符将`SupervisorJob()`和`Dispatchers.Main.immediate`组合在一起，创建了一个新的协程上下文。





### 2. 添加消息回调

在上面消息超时管理的基础上写出来的

消息回调

<img src="assets/image-20231120161730293.png" alt="IMSendCallback" style="zoom:67%;" />

 MessageManager.java，保存发送的消息

<img src="assets/image-20231120161504887.png" alt="MessageManager" style="zoom:67%;" />

sendMsg

<img src="assets/image-20231120161645795.png" alt="sendMsg" style="zoom:67%;" />

## sim-sever

ResultJson：统一返回消息结果

仅包括认证和服务端报告

```java
package com.sy.im.common.result;

import com.alibaba.fastjson.JSONObject;

import java.util.HashMap;

/**
 * 返回 一个map对象
 * k: status    v: 1 或 -1
 * k: msg       v: 消息
 * k: data      v: 也是一个map对象
 */
public class ResultJson extends HashMap<String, Object> {
    private static final long serialVersionUID = 1L;

    private static final Integer SUCCESS_STATUS = 1;
    private static final Integer ERROR_STATUS = -1;

    private static final String SUCCESS_MSG = "操作成功";


    public ResultJson() {
        super();
    }

    /**
     * 有参构造
     * @param status 状态： -1 失败，1 成功
     * @param msg 成功或失败消息
     */
    public ResultJson(int status, String msg){
        super();
        put("status", status);
        put("msg", msg);
    }

    public int getStatus(){
        return (int) get("status");
    }


    public static ResultJson success(){
        return new  ResultJson(SUCCESS_STATUS,SUCCESS_MSG);
    }

    public static ResultJson success(String msg){
        return new  ResultJson(SUCCESS_STATUS,msg);
    }

    public static ResultJson error(String msg){
        return new  ResultJson(ERROR_STATUS,msg);
    }

    public ResultJson setData(String key, Object obj) {
        @SuppressWarnings("unchecked")
        HashMap<String, Object> data = (HashMap<String, Object>) get("data");
        if (data == null) {
            data = new HashMap<String, Object>();
            put("data", data);
        }
        data.put(key, obj);
        return this;
    }

    /**
     * 返回JSON字符串
     */
    @Override
    public String toString() {
        return JSONObject.toJSONString(this);
    }
}
```
