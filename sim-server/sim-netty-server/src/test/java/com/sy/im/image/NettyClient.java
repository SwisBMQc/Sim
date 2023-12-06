package com.sy.im.image;

import com.alibaba.fastjson.JSONObject;
import com.google.protobuf.ByteString;
import com.sy.im.common.constant.APITag;
import com.sy.im.common.constant.MessageType;
import com.sy.im.protobuf.MessageProtobuf;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
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
public class
NettyClient {
    public static void main(String[] args) {
        EventLoopGroup group = new NioEventLoopGroup();

        try {
            Bootstrap bootstrap = new Bootstrap();
            bootstrap.group(group)
                    .channel(NioSocketChannel.class)
                    .handler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            ChannelPipeline pipeline = ch.pipeline();

//                            pipeline.addLast("frameEncoder", new LengthFieldPrepender(2)); // 长度字段所占字节为2
                            /*
                            对消息进行解码
                            最大帧长度为65535;
                            长度字段的偏移量为0;
                            长度字段的长度为2;
                            从长度字段末尾起，还有0个字节到内容;
                            从头剥离2个字节。
                             */
//                            pipeline.addLast("frameDecoder", new LengthFieldBasedFrameDecoder(65535,
//                                    0, 2, 0, 2)); // 帧解码器
                            pipeline.addLast("frameEncoder", new LengthFieldPrepender(3));
                            pipeline.addLast("frameDecoder", new LengthFieldBasedFrameDecoder((2<<(3*8-1)), // 16MB
                                    0, 3, 0, 3));
                            // 增加protobuf编解码支持
                            pipeline.addLast(new ProtobufEncoder());
                            pipeline.addLast(new ProtobufDecoder(MessageProtobuf.Msg.getDefaultInstance()));
                            pipeline.addLast(new FileClientHandler());
                        }
                    });

            ChannelFuture future = bootstrap.connect("localhost", 10001).sync();

            String imagePath = "C:\\Users\\soyo1\\Pictures\\Saved Pictures\\avatar1.jpg";


            try {
                // 读取图片文件为字节数组
                byte[] imageBytes = Files.readAllBytes(Paths.get(imagePath));


                // 创建JSON对象并将Base64字符串放入其中
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("api", APITag.UploadProfile);

                MessageProtobuf.Head head = MessageProtobuf.Head.newBuilder()
                        .setMsgId("123")
                        .setToken("eyJhbGciOiJIUzUxMiIsInppcCI6IkdaSVAifQ.H4sIAAAAAAAAAKtWKi5NUrJScgwN8dANDXYNUtJRSq0oULIyNDcwMjAyNjMx0FEqLU4tykvMTQWqK0ktLlGqBQDWTmVANgAAAA.CBwJ4yeAANNUc2InzKznzSFfQHuRVzwfz16oOCqg0BEoCTp02njc_Xn8EZcuhej9BCS-ndWfKI4SdnSLHRnogQ")
                        .setMsgType(MessageType.REQUEST.getMsgType())
                        .setFromId("1")
                        .setExtend(jsonObject.toString())
                        .build();


                MessageProtobuf.Msg message = MessageProtobuf.Msg.newBuilder()
                        .setHead(head)
//                        .setBody(ByteString.copyFrom("hello".getBytes()))
                        .setBody(ByteString.copyFrom(imageBytes))
                        .build();
                future.channel().writeAndFlush(message);

            } catch (Exception e) {
                e.printStackTrace();
            }

            future.channel().closeFuture().sync();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            group.shutdownGracefully();
        }
    }
}

class FileClientHandler extends ChannelInboundHandlerAdapter {
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        System.out.println("Client connected to server");
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }
}

