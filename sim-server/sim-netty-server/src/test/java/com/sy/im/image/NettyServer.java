package com.sy.im.image;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.google.protobuf.ByteString;
import com.sy.im.protobuf.MessageProtobuf;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;
import io.netty.handler.codec.protobuf.ProtobufDecoder;
import io.netty.handler.codec.protobuf.ProtobufEncoder;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Base64;

/**
 * @Author：sy
 * @Date：2023/11/28
 */
public class NettyServer {
    public static void main(String[] args) {
        EventLoopGroup bossGroup = new NioEventLoopGroup(1);
        EventLoopGroup workerGroup = new NioEventLoopGroup();

        try {
            ServerBootstrap serverBootstrap = new ServerBootstrap();
            serverBootstrap.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            ChannelPipeline pipeline = ch.pipeline();
                            pipeline.addLast("frameEncoder", new LengthFieldPrepender(3));
                            pipeline.addLast("frameDecoder", new LengthFieldBasedFrameDecoder(16777216,
                                    0, 3, 0, 3));
                            pipeline.addLast(new ProtobufDecoder(MessageProtobuf.Msg.getDefaultInstance()));
                            pipeline.addLast(new ProtobufEncoder());
                            pipeline.addLast(new FileServerHandler());
                        }
                    });

            ChannelFuture future = serverBootstrap.bind(10001).sync();
            future.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }
}

class FileServerHandler extends SimpleChannelInboundHandler<MessageProtobuf.Msg> {

    @Override
    public void channelRead0(ChannelHandlerContext ctx, MessageProtobuf.Msg msg) throws Exception {

        MessageProtobuf.Head head = msg.getHead();

        JSONObject extend = JSON.parseObject(head.getExtend());
        System.out.println(extend.getString("api"));
//        String base64Image = msg.getBody();
        // 解码Base64字符串为字节数组
//        byte[] imageBytes = Base64.getDecoder().decode(base64Image);
        byte[] imageBytes = msg.getBody().toByteArray();

//        System.out.println(new String(imageBytes));

        // 图像文件保存路径
        String outputPath = "C:\\Users\\soyo1\\Pictures\\Saved Pictures\\test.jpg";

        try {
            // 将字节数组写入图像文件
            Files.write(Paths.get(outputPath), imageBytes);
            System.out.println("Image saved successfully to: " + outputPath);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }
}
