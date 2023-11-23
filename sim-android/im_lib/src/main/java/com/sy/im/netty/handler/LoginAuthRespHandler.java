package com.sy.im.netty.handler;

import android.util.Log;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.sy.im.message.MessageManager;
import com.sy.im.netty.NettyTcpClient;
import com.sy.im.protobuf.MessageProtobuf;
import com.sy.im.message.MessageType;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.SimpleChannelInboundHandler;

/**
 * 握手认证处理
 */
public class LoginAuthRespHandler extends SimpleChannelInboundHandler<MessageProtobuf.Msg> {

    private NettyTcpClient imsClient;

    public LoginAuthRespHandler(NettyTcpClient imsClient) {
        this.imsClient = imsClient;
    }


    @Override
    protected void channelRead0(ChannelHandlerContext ctx, MessageProtobuf.Msg handshakeRespMsg) throws Exception {
        if (handshakeRespMsg == null || handshakeRespMsg.getHead() == null) {
            return;
        }

        if (MessageType.HANDSHAKE.getMsgType() == handshakeRespMsg.getHead().getMsgType()) { // 握手消息
            Log.i("sim-loginAuth","收到服务端握手响应消息，message=" + handshakeRespMsg);
            int status = -1;
            String reason = "";
            try {
                // 从消息中获得状态
                JSONObject resultJson = JSON.parseObject(handshakeRespMsg.getHead().getExtend());
                status = resultJson.getIntValue("status");
                reason = resultJson.getString("reason");

            } finally {
                if (status == 1) { // 握手成功
                    MessageManager.get(handshakeRespMsg.getHead().getMsgId()).onSuccess(handshakeRespMsg);

                    // 发送心跳消息
                    MessageProtobuf.Msg heartbeatMsg = imsClient.getHeartbeatMsg();
                    if (heartbeatMsg == null) return;
                    Log.i("sim-loginAuth","发送心跳消息：" + heartbeatMsg + "当前心跳间隔为：" + imsClient.getHeartbeatInterval() + "ms\n");
                    imsClient.sendMsg(heartbeatMsg);

                    // 超时消息重发
                    imsClient.getMsgTimeoutTimerManager().onResetConnected();

                    // 添加心跳消息管理
                    imsClient.addHeartbeatHandler();
                } else {
                    MessageManager.get(handshakeRespMsg.getHead().getMsgId()).onError(reason);
//                    imsClient.resetConnect(false); // 握手失败，触发重连
                }
            }
        } else{
            // 消息透传
            ctx.fireChannelRead(handshakeRespMsg);
        }
    }
}
