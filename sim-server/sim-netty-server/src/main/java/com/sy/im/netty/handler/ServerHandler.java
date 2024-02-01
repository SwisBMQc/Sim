package com.sy.im.netty.handler;

import com.sy.im.common.constant.MessageType;
import com.sy.im.common.result.ResultJson;
import com.sy.im.netty.util.MsgUtil;
import com.sy.im.protobuf.MessageProtobuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ServerHandler extends SimpleChannelInboundHandler<MessageProtobuf.Msg> {
    private final static Logger LOGGER = LoggerFactory.getLogger(ServerHandler.class);

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, MessageProtobuf.Msg msg) throws Exception {
        int msgType = msg.getHead().getMsgType();


        // 单聊消息或群聊消息 返回给客户端消息发送状态报告
        if (msgType == MessageType.SINGLE_CHAT.getMsgType() ||
                msgType == MessageType.GROUP_CHAT.getMsgType()){

            LOGGER.info("收到消息"+msg.getHead());

            MessageProtobuf.Head sentReportHead = MessageProtobuf
                    .Head.newBuilder()
                    .setMsgId(msg.getHead().getMsgId())
                    .setMsgType(MessageType.SERVER_MSG_SENT_STATUS_REPORT.getMsgType())
                    .setTimestamp(System.currentTimeMillis()).build();

            MsgUtil.respMsg(LOGGER,ctx,msg,sentReportHead, ResultJson.success());
        }

    }

}