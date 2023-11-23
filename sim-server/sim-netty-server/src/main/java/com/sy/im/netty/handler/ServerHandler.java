package com.sy.im.netty.handler;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.sy.im.common.constant.MessageType;
import com.sy.im.common.protobuf.MessageProtobuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ServerHandler extends SimpleChannelInboundHandler<MessageProtobuf.Msg> {
    private final static Logger LOGGER = LoggerFactory.getLogger(ServerHandler.class);

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, MessageProtobuf.Msg msg) throws Exception {
        int msgType = msg.getHead().getMsgType();

        // 心跳消息
        if (msgType == MessageType.HEARTBEAT.getMsgType()){
            ctx.channel().writeAndFlush(msg);
        }

        // 单聊消息或群聊消息 返回给客户端消息发送状态报告
        if (msgType == MessageType.SINGLE_CHAT.getMsgType() ||
                msgType == MessageType.GROUP_CHAT.getMsgType()){

            MessageProtobuf.Msg.Builder sentReportMsgBuilder = MessageProtobuf.Msg.newBuilder();
            MessageProtobuf.Head.Builder sentReportHeadBuilder = MessageProtobuf
                    .Head.newBuilder()
                    .setMsgId(msg.getHead().getMsgId())
                    .setMsgType(MessageType.SERVER_MSG_SENT_STATUS_REPORT.getMsgType())
                    .setTimestamp(System.currentTimeMillis())
                    .setStatusReport(1);
            MessageProtobuf.Msg backMsg = sentReportMsgBuilder.setHead(sentReportHeadBuilder.build()).build();
            ctx.channel().writeAndFlush(backMsg);
        }

    }

}