package com.sy.im.netty.handler;

import android.util.Log;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.sy.im.message.MessageManager;
import com.sy.im.message.MessageType;
import com.sy.im.protobuf.MessageProtobuf;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

/**
 * API 响应处理类
 * @Author：sy
 * @Date：2023/11/21
 */
public class APIRespHandler extends SimpleChannelInboundHandler<MessageProtobuf.Msg> {

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, MessageProtobuf.Msg msg) throws Exception {
        if (msg == null || msg.getHead() == null) {
            return;
        }

        if (MessageType.REQUEST.getMsgType() == msg.getHead().getMsgType()) {
            Log.i("sim-apiResp","收到api响应消息，message=" + msg);
            int status = -1;
            String reason = "";

            try {
                // 从消息中获得状态
                JSONObject resultJson = JSON.parseObject(msg.getHead().getExtend());
                status = resultJson.getIntValue("status");
                reason = resultJson.getString("reason");

            } finally {
                if (status == 1) {
                    MessageManager.get(msg.getHead().getMsgId()).onSuccess(msg);
                } else {
                    MessageManager.get(msg.getHead().getMsgId()).onError(reason);
                }
            }
        } else {
            ctx.fireChannelRead(msg);
        }
    }
}
