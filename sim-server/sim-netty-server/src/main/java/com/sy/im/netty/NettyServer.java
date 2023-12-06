package com.sy.im.netty;

import com.sy.im.netty.handler.ServerHandler;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

/**
 * netty 服务器
 */
@Component
public class NettyServer {

    private final static Logger LOGGER = LoggerFactory.getLogger(ServerHandler.class);

    @Value("${im.netty.server.port}")
    private int port;

    private EventLoopGroup boss = new NioEventLoopGroup(); // 主线程组
    private EventLoopGroup work = new NioEventLoopGroup(); // 工作线程组

    @Autowired
    NettyServerInit nettyServerInit;

    @PostConstruct  // 该方法将在 Spring Boot 应用启动时自动调用
    public void start(){

        ServerBootstrap server = new ServerBootstrap();
        try {
            server
                    .group(boss, work)
                    .channel(NioServerSocketChannel.class)
                    .option(ChannelOption.SO_BACKLOG, 1024) // 连接缓冲池的大小
                    .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 5000) // 设置连接超时时间为5秒（5秒内完成连接）
                    .childOption(ChannelOption.SO_KEEPALIVE, true) // 只保留活跃连接
                    .childOption(ChannelOption.TCP_NODELAY, true) // 无延迟发送，关闭Nagle算法
                    .childHandler(nettyServerInit);

            ChannelFuture future = server.bind(port).sync();
            LOGGER.info("netty服务端程序已启动... 端口为：" + port);
            future.channel().closeFuture().sync();
            LOGGER.info("netty服务端关闭...");
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            boss.shutdownGracefully();
            work.shutdownGracefully();
        }
    }

}
