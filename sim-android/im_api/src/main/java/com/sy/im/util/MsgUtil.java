package com.sy.im.util;

import android.util.Log;

import com.alibaba.fastjson.JSONObject;
import com.google.protobuf.ByteString;
import com.sy.im.client.IMSClientBootstrap;
import com.sy.im.interf.IMSendCallback;
import com.sy.im.message.MessageType;
import com.sy.im.protobuf.MessageProtobuf;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

/**
 * 发送消息工具类
 * @Author：sy
 * @Date：2023/11/27
 */
public class MsgUtil {

    private static String token;

    public static void setToken(String sessionId){
        token = sessionId;
    }

    public static String getToken() {
        return token;
    }

    /**
     * 发送请求消息
     * @param userId
     * @param messageType
     * @param callback
     * @param jsonObject
     */
    public static void requestMsg(String userId, MessageType messageType, IMSendCallback callback, JSONObject jsonObject) {
        // 构建消息
        MessageProtobuf.Head head = MessageProtobuf.Head.newBuilder()
                .setMsgId(MsgIdGenerator.generateMsgId())
                .setToken(token)
                .setFromId(userId)
                .setMsgType(messageType.getMsgType())
                .setExtend(jsonObject.toString())
                .build();

        MessageProtobuf.Msg msg = MessageProtobuf.Msg.newBuilder().setHead(head).build();

        // 发送消息
        Log.i("sim-api","发送消息\tmessage："+msg.getHead());
        IMSClientBootstrap.getInstance().sendMessage(msg, callback);
    }

    /**
     * 发送文本消息
     * @param fromId
     * @param toId
     * @param messageType
     * @param content
     */
    public static void textMsg(String fromId, String toId, MessageType messageType, String content){

        MessageProtobuf.Head head = MessageProtobuf.Head.newBuilder()
                .setMsgId(MsgIdGenerator.generateMsgId())
                .setToken(token)
                .setFromId(fromId)
                .setToId(toId)
                .setMsgType(messageType.getMsgType())
                .setMsgContentType(MessageType.MessageContentType.TEXT.getMsgContentType())
                .build();

        MessageProtobuf.Msg msg = MessageProtobuf.Msg.newBuilder()
                .setHead(head)
                .setBody(ByteString.copyFrom(content.getBytes(StandardCharsets.UTF_8)))
                .build();

        // 发送消息
        Log.i("sim-api","发送消息\tmessage："+msg.getHead());
        IMSClientBootstrap.getInstance().sendMessage(msg);
    }

    /**
     * 发送图片消息
     * @param fromId
     * @param toId
     * @param messageType 单聊/群聊
     * @param imageBytes 图片的字符数组
     */
    public static void imageMsg(String fromId, String toId, MessageType messageType, byte[] imageBytes){

        MessageProtobuf.Head head = MessageProtobuf.Head.newBuilder()
                .setMsgId(MsgIdGenerator.generateMsgId())
                .setFromId(fromId)
                .setToId(toId)
                .setMsgType(messageType.getMsgType())
                .setMsgContentType(MessageType.MessageContentType.IMAGE.getMsgContentType())
                .build();

        MessageProtobuf.Msg msg = MessageProtobuf.Msg.newBuilder()
                .setHead(head)
                .setBody(ByteString.copyFrom(imageBytes))
                .build();

        // 发送消息
        Log.i("sim-api","发送消息\tmessage："+msg.getHead());
        IMSClientBootstrap.getInstance().sendMessage(msg);
    }



}
