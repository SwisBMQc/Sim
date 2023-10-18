package com.sy.im.client;

import com.alibaba.fastjson.JSONObject;
import com.sy.im.interf.OnEventListener;
import com.sy.im.protobuf.MessageProtobuf;
import com.sy.im.ulit.MessageType;

import java.util.UUID;

public class IMSEventListener implements OnEventListener {

    private String userId;
    private String token;

    public IMSEventListener(String userId, String token) {
        this.userId = userId;
        this.token = token;
    }

    @Override
    public void dispatchMsg(MessageProtobuf.Msg msg) {
        System.out.println("方法消息给应用层IMSEventListener dispatchMsg："+msg);
    }

    @Override
    public boolean isNetworkAvailable() {
        return true;
    }

    @Override
    public int getReconnectInterval() {
        return 0;
    }

    @Override
    public int getConnectTimeout() {
        return 0;
    }

    @Override
    public MessageProtobuf.Msg getHandshakeMsg() {
        // 携带信息
        JSONObject jsonObj = new JSONObject();
        jsonObj.put("token", token);

        // 消息头部
        MessageProtobuf.Head.Builder headBuilder = MessageProtobuf.Head.newBuilder()
                .setMsgId(UUID.randomUUID().toString())
                .setMsgType(MessageType.HANDSHAKE.getMsgType())
                .setFromId(userId)
                .setExtend(jsonObj.toString())
                .setTimestamp(System.currentTimeMillis());
        // 构建消息
        MessageProtobuf.Msg.Builder builder = MessageProtobuf
                .Msg.newBuilder()
                .setHead(headBuilder.build());

        return builder.build();
    }

    @Override
    public MessageProtobuf.Msg getHeartbeatMsg() {
        MessageProtobuf.Head.Builder headBuilder = MessageProtobuf.Head.newBuilder()
                .setMsgId(UUID.randomUUID().toString())
                .setMsgType(MessageType.HEARTBEAT.getMsgType())
                .setFromId(userId)
                .setTimestamp(System.currentTimeMillis());
        // 构建消息
        MessageProtobuf.Msg.Builder builder = MessageProtobuf
                .Msg.newBuilder()
                .setHead(headBuilder.build());

        return builder.build();
    }

    @Override
    public int getServerSentReportMsgType() {
        return MessageType.SERVER_MSG_SENT_STATUS_REPORT.getMsgType();
    }

    @Override
    public int getClientReceivedReportMsgType() {
        return MessageType.CLIENT_MSG_RECEIVED_STATUS_REPORT.getMsgType();
    }

    @Override
    public int getResendCount() {
        return 0;
    }

    @Override
    public int getResendInterval() {
        return 0;
    }
}
