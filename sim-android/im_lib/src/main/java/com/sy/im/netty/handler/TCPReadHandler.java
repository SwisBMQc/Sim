package com.sy.im.netty.handler;

import android.util.Log;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.sy.im.interf.IMSendCallback;
import com.sy.im.message.MessageManager;
import com.sy.im.message.MessageType;
import com.sy.im.netty.NettyTcpClient;
import com.sy.im.protobuf.MessageProtobuf;
import com.sy.im.util.IMSConfig;

import java.util.UUID;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.util.internal.StringUtil;

/**
 * 消息接收处理
 */
public class TCPReadHandler extends SimpleChannelInboundHandler<MessageProtobuf.Msg> {

    private NettyTcpClient imsClient;

    public TCPReadHandler(NettyTcpClient imsClient) {
        this.imsClient = imsClient;
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception { // 当前连接已经断开
        super.channelInactive(ctx);
        Log.e("sim-tcp","TCPReadHandler channelInactive() 当前连接断开");
        Channel channel = ctx.channel();
        if (channel != null) {
            channel.close();
            ctx.close();
        }

        // 触发重连
        imsClient.resetConnect(false);
    }



    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        super.exceptionCaught(ctx, cause);
        Log.e("sim-tcp","TCPReadHandler exceptionCaught()");
        Channel channel = ctx.channel();
        if (channel != null) {
            channel.close();
            ctx.close();
        }

        // 触发重连
        imsClient.resetConnect(false);
    }


    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, MessageProtobuf.Msg msg) throws Exception {
        if (msg == null || msg.getHead() == null) {
            return;
        }

        MessageProtobuf.Head head = msg.getHead();
        String msgId = head.getMsgId();
        int msgType = head.getMsgType();

        // 如果是服务端发的报告消息
        if (msgType == MessageType.SERVER_MSG_SENT_STATUS_REPORT.getMsgType()) {

            int statusReport = msg.getHead().getStatusReport();
            Log.i("sim-tcp",String.format("服务端状态报告：「%d」, 1代表成功，0代表失败", statusReport));

            if (statusReport == IMSConfig.DEFAULT_REPORT_SERVER_SEND_MSG_SUCCESSFUL) {
                // 成功，清除消息库存
                Log.i("sim-tcp","收到服务端消息发送状态报告，message=" + msg + "，从超时管理器移除");
                imsClient.getMsgTimeoutTimerManager().remove(msg.getHead().getMsgId());
            }

        } else {
            // 其它消息
            // 收到消息后，立马给服务端回一条消息接收状态报告
            Log.i("sim","收到消息，message=" + msg);
            MessageProtobuf.Msg receivedReportMsg = buildReceivedReportMsg(msg.getHead().getMsgId());
            if(receivedReportMsg != null) {
                imsClient.sendMsg(receivedReportMsg);
            }
        }

        // 接收消息，由消息转发器转发到应用层
        imsClient.getMsgDispatcher().receivedMsg(msg);
    }

    /**
     * 构建客户端消息接收状态报告
     * @param msgId
     * @return
     */
    private MessageProtobuf.Msg buildReceivedReportMsg(String msgId) {
        if (StringUtil.isNullOrEmpty(msgId)) {
            return null;
        }

        MessageProtobuf.Msg.Builder builder = MessageProtobuf.Msg.newBuilder();
        MessageProtobuf.Head.Builder headBuilder =
                MessageProtobuf.Head
                        .newBuilder()
                        .setMsgId(UUID.randomUUID().toString())
                        .setMsgType(MessageType.CLIENT_MSG_RECEIVED_STATUS_REPORT.getMsgType())
                        .setTimestamp(System.currentTimeMillis());

        JSONObject jsonObj = new JSONObject();
        jsonObj.put("msgId", msgId);
        headBuilder.setExtend(jsonObj.toString());
        builder.setHead(headBuilder.build());

        return builder.build();
    }
}
