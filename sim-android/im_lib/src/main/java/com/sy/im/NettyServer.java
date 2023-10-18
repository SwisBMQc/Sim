package com.sy.im;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.sy.im.protobuf.MessageProtobuf;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;
import io.netty.handler.codec.protobuf.ProtobufDecoder;
import io.netty.handler.codec.protobuf.ProtobufEncoder;

public class NettyServer {

    public static void main(String[] args) {
        System.out.println("哈喽");

        EventLoopGroup boss = new NioEventLoopGroup();
        EventLoopGroup worker = new NioEventLoopGroup();

        try {
            ChannelFuture channelFuture = new ServerBootstrap()
                    .group(boss, worker)
                    .channel(NioServerSocketChannel.class)
                    .option(ChannelOption.SO_BACKLOG, 1024) // 连接缓冲池的大小
                    .childOption(ChannelOption.SO_KEEPALIVE, true) // 只保留活跃连接
                    .childOption(ChannelOption.TCP_NODELAY, true) // 无延迟发送
                    .childHandler(new ChannelInitializer<NioSocketChannel>() {
                        @Override
                        protected void initChannel(NioSocketChannel channel) throws Exception {
                            // 获得管道
                            ChannelPipeline pipeline = channel.pipeline();
                            // 编解码
                            pipeline.addLast("frameEncoder", new LengthFieldPrepender(2));
                            pipeline.addLast("frameDecoder", new LengthFieldBasedFrameDecoder(65535,
                                    0, 2, 0, 2));
                            pipeline.addLast(new ProtobufDecoder(MessageProtobuf.Msg.getDefaultInstance()));
                            pipeline.addLast(new ProtobufEncoder());
                            //处理类
                            pipeline.addLast(new ServerHandler());
                        }
                    })
                    .bind(9000).sync();

            System.out.println("server start ...... ");
            //等待服务端监听端口关闭
            channelFuture.channel().closeFuture().sync();


        } catch (Exception e) {
            e.printStackTrace();
            boss.shutdownGracefully();
            worker.shutdownGracefully();
        }
    }
}

class ServerHandler extends ChannelInboundHandlerAdapter {
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        super.channelActive(ctx);
        System.out.println("ServerHandler channelActive()" + ctx.channel().remoteAddress());
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        MessageProtobuf.Msg message = (MessageProtobuf.Msg) msg;
        System.out.println("收到来自客户端的消息：" + message);
        int msgType = message.getHead().getMsgType();
        switch (msgType) {
            // 握手消息
            case 1001: {
                String fromId = message.getHead().getFromId();
                JSONObject jsonObj = JSON.parseObject(message.getHead().getExtend());
                String token = jsonObj.getString("token");
                JSONObject resp = new JSONObject();
                if (token.equals("token_" + fromId)) {
                    resp.put("status", 1);
                } else {
                    resp.put("status", -1);
                }

                message = message.toBuilder().setHead(message.getHead().toBuilder().setExtend(resp.toString()).build()).build();
                ctx.channel().writeAndFlush(message);
                break;
            }

            // 心跳消息
            case 1002: {
                // 收到心跳消息，原样返回
                ctx.channel().writeAndFlush(message);
                break;
            }

//            case 2001: {
//                // 收到2001或3001消息，返回给客户端消息发送状态报告
//                MessageProtobuf.Msg.Builder sentReportMsgBuilder = MessageProtobuf.Msg.newBuilder();
//                MessageProtobuf.Head.Builder sentReportHeadBuilder = MessageProtobuf
//                        .Head.newBuilder()
//                        .setMsgId(message.getHead().getMsgId())
//                        .setMsgType(1010)
//                        .setTimestamp(System.currentTimeMillis())
//                        .setStatusReport(1);
//                MessageProtobuf.Msg backMsg = sentReportMsgBuilder.setHead(sentReportHeadBuilder.build()).build();
//                ctx.channel().writeAndFlush(backMsg);
//                break;
//            }

            default:
                break;
        }
    }

}