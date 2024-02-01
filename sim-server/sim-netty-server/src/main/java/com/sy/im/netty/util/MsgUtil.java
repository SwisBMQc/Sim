package com.sy.im.netty.util;

import com.sy.im.common.constant.MessageType;
import com.sy.im.common.result.ResultJson;
import com.sy.im.protobuf.MessageProtobuf;
import io.netty.channel.ChannelHandlerContext;
import org.slf4j.Logger;

/**
 * 发送消息工具类
 * @Author：sy
 * @Date：2023/11/27
 */
public class MsgUtil {

    /**
     * 发送响应消息
     * @param logger
     * @param ctx
     * @param msg
     * @param head
     * @param resultJson
     */
    public static void respMsg(Logger logger,
                               ChannelHandlerContext ctx,
                               MessageProtobuf.Msg msg,
                               MessageProtobuf.Head head,
                               ResultJson resultJson) {

        logger.info(resultJson.toString());
        head = head.toBuilder().setExtend(resultJson.toString())
                .setTimestamp(System.currentTimeMillis()).build();
        msg = msg.toBuilder().setHead(head).build();
        ctx.writeAndFlush(msg);
    }

    public static MessageProtobuf.Msg respMsg(Logger logger, ResultJson resultJson) {

        logger.info(resultJson.toString());
        MessageProtobuf.Head head = MessageProtobuf.Head.newBuilder()
                .setMsgType(MessageType.HANDSHAKE.getMsgType())
                .setExtend(resultJson.toString())
                .setTimestamp(System.currentTimeMillis()).build();

        return MessageProtobuf.Msg.newBuilder().setHead(head).build();
    }
}
