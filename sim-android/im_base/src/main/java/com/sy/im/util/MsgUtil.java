package com.sy.im.util;

import android.util.Log;

import com.alibaba.fastjson.JSONObject;
import com.google.protobuf.ByteString;
import com.sy.im.client.IMSClientBootstrap;
import com.sy.im.interf.IMSendCallback;
import com.sy.im.message.MessageType;
import com.sy.im.protobuf.MessageProtobuf;

import java.nio.charset.StandardCharsets;

/**
 * 发送消息工具类
 * @Author：sy
 * @Date：2023/11/27
 */
public class MsgUtil {

    private static String userId;

    private static String token;

    public static void set(String userId,String token){
        MsgUtil.userId = userId;
        MsgUtil.token = token;
    }

    public static String getUserId() {
        return userId;
    }

    public static String getToken() {
        return token;
    }

    /**
     * 发送请求消息
     * @param fromId
     * @param messageType
     * @param callback
     * @param jsonObject
     */
    public static void requestMsg(String fromId, MessageType messageType, IMSendCallback callback, JSONObject jsonObject) {
        // 构建消息
        MessageProtobuf.Head head = MessageProtobuf.Head.newBuilder()
                .setMsgId(MsgIdGenerator.generateMsgId())
                .setToken(getToken())
                .setFromId(fromId)
                .setMsgType(messageType.getMsgType())
                .setExtend(jsonObject.toString())
                .build();

        MessageProtobuf.Msg msg = MessageProtobuf.Msg.newBuilder().setHead(head).build();

        // 发送消息
        Log.i("sim-api","发送消息\tmessage："+msg.getHead());
        IMSClientBootstrap.getInstance().sendMessage(msg, callback);
    }

    public static void requestMsg(MessageType messageType, IMSendCallback callback, JSONObject jsonObject){
        requestMsg(getUserId(),messageType,callback,jsonObject);
    }

    public static void requestMsg(MessageProtobuf.Head.Builder headBuilder,IMSendCallback callback){
        // 构建消息
        MessageProtobuf.Head head = headBuilder
                .setMsgId(MsgIdGenerator.generateMsgId())
                .setToken(getToken())
                .setFromId(getUserId())
                .build();

        MessageProtobuf.Msg msg = MessageProtobuf.Msg.newBuilder().setHead(head).build();

        // 发送消息
        Log.i("sim-api","发送消息\tmessage："+msg.getHead());
        IMSClientBootstrap.getInstance().sendMessage(msg, callback);
    }

    /**
     * 发送文本消息
     * @param toId
     * @param messageType
     * @param content
     */
    public static void textMsg(String toId, MessageType messageType, String content){

        MessageProtobuf.Head head = MessageProtobuf.Head.newBuilder()
                .setMsgId(MsgIdGenerator.generateMsgId())
                .setToken(getToken())
                .setFromId(getUserId())
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
