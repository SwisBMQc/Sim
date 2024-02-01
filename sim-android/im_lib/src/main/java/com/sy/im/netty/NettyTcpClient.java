package com.sy.im.netty;

import android.util.Log;

import com.sy.im.interf.IMSClient;
import com.sy.im.interf.IMSConnectStatusCallback;
import com.sy.im.interf.OnEventListener;
import com.sy.im.message.MessageManager;
import com.sy.im.netty.handler.HeartbeatHandler;
import com.sy.im.netty.handler.TCPReadHandler;
import com.sy.im.protobuf.MessageProtobuf;
import com.sy.im.util.ExecutorServiceFactory;
import com.sy.im.util.IMSConfig;
import com.sy.im.message.MsgDispatcher;
import com.sy.im.message.MsgTimeoutTimerManager;

import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.TimeUnit;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.timeout.IdleStateHandler;
import io.netty.util.internal.StringUtil;

public class NettyTcpClient implements IMSClient {

    private static volatile  NettyTcpClient instance;   // 懒汉式单例模式
    private Bootstrap bootstrap;
    private Channel channel;

    private String currentHost = null;  // 当前连接host
    private int currentPort = -1;       // 当前连接port

    private boolean isClosed = false;
    private CopyOnWriteArrayList<String> serverUrlList;
    private OnEventListener eventListener;
    private IMSConnectStatusCallback connectStatusCallback;

    private MsgDispatcher msgDispatcher;        // 消息转发器
    private ExecutorServiceFactory loopGroup;   // 线程池工厂，负责调度重连及心跳线程

    private boolean isReconnecting = false;                                         // 是否正在进行重连
    private int connectStatus = IMSConfig.CONNECT_STATE_FAILURE;                    // ims连接状态，初始化为连接失败
    private int reconnectInterval = IMSConfig.DEFAULT_RECONNECT_BASE_DELAY_TIME;    // 重连间隔时长
    private int connectTimeout = IMSConfig.DEFAULT_CONNECT_TIMEOUT;                 // 连接超时时长

    private int appStatus = IMSConfig.APP_STATUS_FOREGROUND;                                      // app前后台状态
    private int heartbeatInterval = IMSConfig.DEFAULT_HEARTBEAT_INTERVAL_FOREGROUND;              // 心跳间隔时间
    private int foregroundHeartbeatInterval = IMSConfig.DEFAULT_HEARTBEAT_INTERVAL_FOREGROUND;    // 应用在前台时心跳间隔时间
    private int backgroundHeartbeatInterval = IMSConfig.DEFAULT_HEARTBEAT_INTERVAL_BACKGROUND;    // 应用在后台时心跳间隔时间

    private int resendCount = IMSConfig.DEFAULT_RESEND_COUNT;            // 消息发送超时重发次数
    private int resendInterval = IMSConfig.DEFAULT_RESEND_INTERVAL;      // 消息发送失败重发间隔时长

    private MsgTimeoutTimerManager msgTimeoutTimerManager;               // 消息发送超时定时器管理

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

    @Override
    public void init(CopyOnWriteArrayList serverUrlList, OnEventListener listener, IMSConnectStatusCallback callback) {
        close();
        isClosed = false;
        this.serverUrlList = serverUrlList;
        this.eventListener = listener;
        this.connectStatusCallback = callback;
        msgDispatcher = new MsgDispatcher();
        msgDispatcher.setOnEventListener(listener);
        loopGroup = new ExecutorServiceFactory();
        loopGroup.initBossLoopGroup();// 初始化重连线程组

        msgTimeoutTimerManager = new MsgTimeoutTimerManager(this);

        resetConnect(true);
    }

    @Override
    public void resetConnect() {
        this.resetConnect(false);
    }

    @Override
    public void resetConnect(boolean isFirst) {
        if (!isFirst) { // 不是首次则需要等待间隔
            try {
                Thread.sleep(IMSConfig.DEFAULT_RECONNECT_INTERVAL);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        if (!isClosed && !isReconnecting) {
            synchronized (this) {
                if (!isClosed && !isReconnecting) {
                    // 标识正在重连
                    isReconnecting = true;
                    // 回调ims连接状态
                    onConnectStatusCallback(IMSConfig.CONNECT_STATE_CONNECTING);
                    // 关闭之前打开的连接channel
                    closeChannel();
                    // 执行重新连接任务
                    loopGroup.execBossTask(new ResetConnectRunnable(isFirst));
                }
            }
        }
    }

    /**
     * 关闭连接，同时释放资源
     */
    @Override
    public void close() {
        if (isClosed) {
            return;
        }

        isClosed = true;

        // 关闭channel
        try {
            closeChannel();
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        // 关闭bootstrap
        try {
            if (bootstrap != null) {
                bootstrap.group().shutdownGracefully();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        try {
            // 释放线程池
            if (loopGroup != null) {
                loopGroup.destroy();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            try {
                if (serverUrlList != null) {
                    serverUrlList.clear();
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }

            isReconnecting = false;
            channel = null;
            bootstrap = null;
        }
    }

    @Override
    public boolean isClosed() {
        return false;
    }

    /**
     * 发送消息
     *
     * @param msg
     */
    @Override
    public void sendMsg(MessageProtobuf.Msg msg) {
        this.sendMsg(msg, false);
    }

    /**
     * 发送消息
     * 重载
     *
     * @param msg
     * @param isJoinTimeoutManager 是否加入发送超时管理器
     */
    @Override
    public void sendMsg(MessageProtobuf.Msg msg, boolean isJoinTimeoutManager) {
        if (msg == null || msg.getHead() == null) {
            Log.e("sim","发送消息失败，消息为空\t");
            return;
        }

        if(!StringUtil.isNullOrEmpty(msg.getHead().getMsgId())) { // 消息id不为空
            if(isJoinTimeoutManager) {
                msgTimeoutTimerManager.add(msg);
            }
        }

        if (channel == null) {
            Log.e("sim","发送消息失败，channel为空\tmessage=" + msg);
            MessageManager.get(msg.getHead().getMsgId()).onError("无连接");
            return;
        }

        try {
            channel.writeAndFlush(msg);
        } catch (Exception ex) {
            Log.e("sim","发送消息失败，reason:" + ex.getMessage() + "\tmessage=" + msg);
            MessageManager.get(msg.getHead().getMsgId()).onError("发送消息失败");
        }
    }

    /**
     * 获取连接超时时长
     *
     * @return
     */
    @Override
    public int getConnectTimeout() {
        if (eventListener != null && eventListener.getConnectTimeout() > 0) {
            return connectTimeout = eventListener.getConnectTimeout();
        }

        return connectTimeout;
    }

    /**
     * 获取应用在前台时心跳间隔时间
     *
     * @return
     */
    @Override
    public int getForegroundHeartbeatInterval() {
        if (eventListener != null && eventListener.getForegroundHeartbeatInterval() > 0) {
            return foregroundHeartbeatInterval = eventListener.getForegroundHeartbeatInterval();
        }

        return foregroundHeartbeatInterval;
    }

    /**
     * 获取应用在前台时心跳间隔时间
     *
     * @return
     */
    @Override
    public int getBackgroundHeartbeatInterval() {
        if (eventListener != null && eventListener.getBackgroundHeartbeatInterval() > 0) {
            return backgroundHeartbeatInterval = eventListener.getBackgroundHeartbeatInterval();
        }

        return backgroundHeartbeatInterval;
    }

    /**
     * 设置app前后台状态
     *
     * @param appStatus
     */
    @Override
    public void setAppStatus(int appStatus) {
        this.appStatus = appStatus;
        if (this.appStatus == IMSConfig.APP_STATUS_FOREGROUND) {
            heartbeatInterval = foregroundHeartbeatInterval;
        } else if (this.appStatus == IMSConfig.APP_STATUS_BACKGROUND) {
            heartbeatInterval = backgroundHeartbeatInterval;
        }

        addHeartbeatHandler();
    }

    /**
     * 获取重连间隔时长
     *
     * @return
     */
    @Override
    public int getReconnectInterval() {
        if (eventListener != null && eventListener.getReconnectInterval() > 0) {
            return reconnectInterval = eventListener.getReconnectInterval();
        }

        return reconnectInterval;
    }



    /**
     * 获取由应用层构造的心跳消息
     *
     * @return
     */
    @Override
    public MessageProtobuf.Msg getHeartbeatMsg() {
        if (eventListener != null) {
            return eventListener.getHeartbeatMsg();
        }

        return null;
    }

    @Override
    public MsgTimeoutTimerManager getMsgTimeoutTimerManager() {
        return msgTimeoutTimerManager;
    }

    /**
     * 获取线程池
     *
     * @return
     */
    public ExecutorServiceFactory getLoopGroup() {
        return loopGroup;
    }

    /**
     * 获取心跳间隔时间
     *
     * @return
     */
    public int getHeartbeatInterval() {
        return this.heartbeatInterval;
    }

    /**
     * 添加心跳消息管理handler
     */
    public void addHeartbeatHandler() {
        if (channel == null || !channel.isActive() || channel.pipeline() == null) { // channel已经关闭或者还未连接成功
            return;
        }

        try {
            // 之前存在的读写超时handler，先移除掉，再重新添加
            if (channel.pipeline().get(IdleStateHandler.class.getSimpleName()) != null) {
                channel.pipeline().remove(IdleStateHandler.class.getSimpleName());
            }
            // 监听心跳，3次心跳没响应，代表连接已断开
            channel.pipeline().addFirst(IdleStateHandler.class.getSimpleName(), new IdleStateHandler(
                    heartbeatInterval * 3, heartbeatInterval, 0, TimeUnit.MILLISECONDS));

            // 移除掉之前的HeartbeatHandler，重新添加
            if (channel.pipeline().get(HeartbeatHandler.class.getSimpleName()) != null) {
                channel.pipeline().remove(HeartbeatHandler.class.getSimpleName());
            }
            if (channel.pipeline().get(TCPReadHandler.class.getSimpleName()) != null) {
                // 把HeartbeatHandler插到TCPReadHandler的前面，HeartbeatHandler-> TCPReadHandler
                channel.pipeline().addBefore(TCPReadHandler.class.getSimpleName(), HeartbeatHandler.class.getSimpleName(),
                        new HeartbeatHandler(this));
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("sim","添加心跳消息管理handler失败，reason：" + e.getMessage());
        }
    }

    /**
     * 获得应用层消息重发次数
     * @return
     */
    @Override
    public int getResendCount() {
        if (eventListener != null && eventListener.getResendCount() != 0) {
            return resendCount = eventListener.getResendCount();
        }

        return resendCount;
    }

    /**
     * 获取应用层消息发送超时重发间隔
     *
     * @return
     */
    @Override
    public int getResendInterval() {
        if (eventListener != null && eventListener.getReconnectInterval() != 0) {
            return resendInterval = eventListener.getResendInterval();
        }

        return resendInterval;
    }

    @Override
    public MsgDispatcher getMsgDispatcher() {
        return msgDispatcher;
    }

    /**
     * 回调ims连接状态
     *
     * @param connectStatus
     */
    public void onConnectStatusCallback(int connectStatus) {
        this.connectStatus = connectStatus;
        switch (connectStatus) {
            case IMSConfig.CONNECT_STATE_CONNECTING:
                System.out.println("sim-client "+"ims连接中...");
                if (connectStatusCallback != null) {
                    connectStatusCallback.onConnecting();
                }
                break;


            case IMSConfig.CONNECT_STATE_SUCCESSFUL:
                System.out.println("sim-client "+String.format("ims连接成功，host『%s』, port『%s』", currentHost, currentPort));
                if (connectStatusCallback != null) {
                    connectStatusCallback.onConnected();
                }
                // 连接成功，发送握手消息
                MessageProtobuf.Msg handshakeMsg = getHandshakeMsg();
                if (handshakeMsg != null) {
                    System.out.println("发送握手消息，message=" + handshakeMsg);
                    sendMsg(handshakeMsg, false);
                } else {
//                    System.err.println("请应用层构建握手消息！");
                }
                break;


            case IMSConfig.LOGIN_AUTH_STATE_FAILURE:
                Log.e("sim-client","ims认证失败");
//                System.err.println("sim-client "+ "ims认证失败");
                if (connectStatusCallback != null) {
                    connectStatusCallback.onLoginAuthFailed();
                }
                break;


            case IMSConfig.CONNECT_STATE_FAILURE:
            default: {
                Log.e("sim-client","ims连接失败");
//                System.err.println("sim-client "+ "ims连接失败");
                if (connectStatusCallback != null) {
                    connectStatusCallback.onConnectFailed();
                }
                break;
            }


        }
    }

    @Override
    public MessageProtobuf.Msg getHandshakeMsg() {
        if (eventListener != null) {
            return eventListener.getHandshakeMsg();
        }
        return null;
    }

    /**
     * 关闭channel
     */
    private void closeChannel() {
        try {
            if (channel != null) {
                channel.close();
                channel.eventLoop().shutdownGracefully();
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("sim-client","关闭channel出错，reason:" + e.getMessage());
//            System.err.println("sim-client "+"关闭channel出错，reason:" + e.getMessage());
        } finally {
            channel = null;
        }
    }

    /**
     * 初始化bootstrap
     */
    private void initBootstrap() {
        EventLoopGroup loopGroup = new NioEventLoopGroup(4);
        bootstrap = new Bootstrap()
        .group(loopGroup).channel(NioSocketChannel.class)
        .option(ChannelOption.SO_KEEPALIVE, true) // 设置该选项以后，如果在两小时内没有数据的通信时，TCP会自动发送一个活动探测数据报文
        .option(ChannelOption.TCP_NODELAY, true) // 设置禁用nagle算法
        .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, getConnectTimeout()) // 设置连接超时时长
        .handler(new TCPChannelInitializer(this)); // 设置初始化Channel
    }

    /**
     * 从应用层获取网络是否可用
     *
     * @return
     */
    private boolean isNetworkAvailable() {
        if (eventListener != null) {
            return eventListener.isNetworkAvailable();
        }

        return false;
    }

    /**
     * 真正连接服务器的地方
     */
    private void toServer() {
        try {
            Log.w("sim-client ","当前线程连接 "+Thread.currentThread().getName());
            channel = bootstrap.connect(currentHost, currentPort).sync().channel();
        } catch (Exception e) {
            try {
                // 线程休眠500毫秒，以防止过于频繁地尝试连接。
                Thread.sleep(500);
            } catch (InterruptedException e1) {
                e1.printStackTrace();
            }
            Log.e("sim-client ",String.format("连接Server(ip[%s], port[%s])失败", currentHost, currentPort));
//            System.err.println("sim-client "+String.format("连接Server(ip[%s], port[%s])失败", currentHost, currentPort));
            channel = null;
            Log.w("sim-client ","当前线程苏醒 channel = null"+Thread.currentThread().getName());

        }
    }

    /**
     * 内部类 重连任务
     */
    private class ResetConnectRunnable implements Runnable {

        private boolean isFirst;

        public ResetConnectRunnable(boolean isFirst) {
            this.isFirst = isFirst;
        }

        @Override
        public void run() {
            // 非首次进行重连，执行到这里即代表已经连接失败，回调连接状态到应用层
            if (!isFirst) {
                onConnectStatusCallback(IMSConfig.CONNECT_STATE_FAILURE);
            }

            try {
                // 重连时，释放工作线程组，也就是停止心跳
                loopGroup.destroyWorkLoopGroup();

                while (!isClosed) {
                    if(!isNetworkAvailable()) {
                        try {
                            Log.e("sim-client ","网络不可用");
//                            System.err.println("sim-client "+"网络不可用");
                            Thread.sleep(2000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        continue;
                    }


                    // 网络可用才进行连接
                    int status;
                    if ((status = reConnect()) == IMSConfig.CONNECT_STATE_SUCCESSFUL) {
                        onConnectStatusCallback(status);
                        // 连接成功，跳出循环
                        break;
                    }

                    if (status == IMSConfig.CONNECT_STATE_FAILURE) {
                        onConnectStatusCallback(status);
                        try {
                            Thread.sleep(IMSConfig.DEFAULT_RECONNECT_INTERVAL);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
            } finally {
                // 标识重连任务停止
                isReconnecting = false;
            }
        }

        /**
         * 重连，首次连接也认为是第一次重连
         *
         * @return
         */
        private int reConnect() {
            // 未关闭才去连接
            if (!isClosed) {
                try {
                    // 先释放EventLoop线程组
                    if (bootstrap != null) {
                        bootstrap.group().shutdownGracefully();
                    }
                } finally {
                    bootstrap = null;
                }

                // 初始化bootstrap
                initBootstrap();
                return connectServer();
            }
            return IMSConfig.CONNECT_STATE_FAILURE;
        }

        /**
         * 连接服务器
         *
         * @return
         */
        private int connectServer() {
            // 如果服务器地址无效，直接回调连接状态，不再进行连接
            // 有效的服务器地址示例：127.0.0.1 8860
            if (serverUrlList == null || serverUrlList.size() == 0) {
                return IMSConfig.CONNECT_STATE_FAILURE;
            }

            // 遍历多个服务器的地址
            for (int i = 0; (!isClosed && i < serverUrlList.size()); i++) {
                String serverUrl = serverUrlList.get(i);
                // 如果服务器地址无效，直接回调连接状态，不再进行连接
                if (StringUtil.isNullOrEmpty(serverUrl)) {
                    return IMSConfig.CONNECT_STATE_FAILURE;
                }

                String[] address = serverUrl.split(" ");
                for (int j = 1; j <= IMSConfig.DEFAULT_RECONNECT_COUNT; j++) {
                    // 如果ims已关闭，或网络不可用，直接回调连接状态，不再进行连接
                    if (isClosed || !isNetworkAvailable()) {
                        return IMSConfig.CONNECT_STATE_FAILURE;
                    }

                    // 回调连接状态
                    if (connectStatus != IMSConfig.CONNECT_STATE_CONNECTING) {
                        onConnectStatusCallback(IMSConfig.CONNECT_STATE_CONNECTING);
                    }
                    System.out.println("sim-client "+String.format("正在进行『%s』的第『%d』次连接，当前重连延时时长为『%dms』", serverUrl, j, j * getReconnectInterval()));

                    try {
                        currentHost = address[0];// 获取host
                        currentPort = Integer.parseInt(address[1]);// 获取port
                        toServer();// 连接服务器

                        // channel不为空，即认为连接已成功
                        if (channel != null) {
                            return IMSConfig.CONNECT_STATE_SUCCESSFUL;
                        } else {
                            // 连接失败，则线程休眠n * 重连间隔时长
                            Thread.sleep(j * getReconnectInterval());
                        }
                    } catch (InterruptedException e) {
                        close();
                        break;// 线程被中断，则强制关闭
                    }
                }
            }

            // 执行到这里，代表连接失败
            return IMSConfig.CONNECT_STATE_FAILURE;
        }
    }

}
