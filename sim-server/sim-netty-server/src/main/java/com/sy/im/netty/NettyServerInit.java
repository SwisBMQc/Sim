package com.sy.im.netty;

import com.sy.im.common.protobuf.MessageProtobuf;
import com.sy.im.netty.handler.APIReqHandler;
import com.sy.im.netty.handler.LoginAuthReqHandler;
import com.sy.im.netty.handler.ServerHandler;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;
import io.netty.handler.codec.protobuf.ProtobufDecoder;
import io.netty.handler.codec.protobuf.ProtobufEncoder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class NettyServerInit extends ChannelInitializer<NioSocketChannel> {

    @Autowired
    LoginAuthReqHandler loginAuthReqHandler;

    @Autowired
    APIReqHandler apiReqHandler;


    @Override
    protected void initChannel(NioSocketChannel channel) throws Exception {
        // 获得管道
        ChannelPipeline pipeline = channel.pipeline();
        // 拆包粘包 编解码
        pipeline.addLast("frameEncoder", new LengthFieldPrepender(2));
        pipeline.addLast("frameDecoder", new LengthFieldBasedFrameDecoder(65535,
                0, 2, 0, 2));
        pipeline.addLast(new ProtobufDecoder(MessageProtobuf.Msg.getDefaultInstance()));
        pipeline.addLast(new ProtobufEncoder());
        //处理类
        pipeline.addLast(loginAuthReqHandler);
        pipeline.addLast(apiReqHandler);
        pipeline.addLast(new ServerHandler());
    }

}