package com.sy.im.netty;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.sy.im.protobuf.MessageProtobuf;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

/**
 * <p>@ProjectName:     NettyChat</p>
 * <p>@ClassName:       LoginAuthRespHandler.java</p>
 * <p>@PackageName:     com.freddy.im</p>
 * <b>
 * <p>@Description:     握手认证消息响应处理handler</p>
 * </b>
 * <p>@author:          FreddyChen</p>
 * <p>@date:            2019/04/07 23:11</p>
 * <p>@email:           chenshichao@outlook.com</p>
 */
public class LoginAuthRespHandler extends ChannelInboundHandlerAdapter {

    private NettyTcpClient imsClient;

    public LoginAuthRespHandler(NettyTcpClient imsClient) {
        this.imsClient = imsClient;
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        MessageProtobuf.Msg handshakeRespMsg = (MessageProtobuf.Msg) msg; // 获得服务器响应消息
        if (handshakeRespMsg == null || handshakeRespMsg.getHead() == null) {
            return;
        }

        MessageProtobuf.Msg handshakeMsg = imsClient.getHandshakeMsg(); // 获得应用层的握手消息
        if (handshakeMsg == null || handshakeMsg.getHead() == null) {
            return;
        }

        int handshakeMsgType = handshakeMsg.getHead().getMsgType(); // 获得消息类型 1001为握手消息
        if (handshakeMsgType == handshakeRespMsg.getHead().getMsgType()) { // 响应消息也为握手消息
            System.out.println("收到服务端握手响应消息，message=" + handshakeRespMsg);
            int status = -1;
            try {
                // 从消息中获得状态
                JSONObject jsonObj = JSON.parseObject(handshakeRespMsg.getHead().getExtend());
                status = jsonObj.getIntValue("status");
            } finally {
                if (status == 1) { // 握手成功
                    // 发送心跳消息
                    MessageProtobuf.Msg heartbeatMsg = imsClient.getHeartbeatMsg();
                    if (heartbeatMsg == null) return;
                    System.out.println("发送心跳消息：" + heartbeatMsg + "当前心跳间隔为：" + imsClient.getHeartbeatInterval() + "ms\n");
                    imsClient.sendMsg(heartbeatMsg);

                    // 超时消息重发
                    imsClient.getMsgTimeoutTimerManager().onResetConnected();

                    // 添加心跳消息管理
                    imsClient.addHeartbeatHandler();
                } else {
                    imsClient.resetConnect(false);// 握手失败，触发重连
                }
            }
        } else {
            // 消息透传
            ctx.fireChannelRead(msg);
        }
    }
}
