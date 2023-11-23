package com.sy.im.api;

import android.util.Log;

import com.alibaba.fastjson.JSONObject;
import com.sy.im.client.IMSClientBootstrap;
import com.sy.im.interf.IMSendCallback;
import com.sy.im.message.MessageType;
import com.sy.im.protobuf.MessageProtobuf;
import com.sy.im.util.APITag;
import com.sy.im.util.MsgIdGenerator;

public class AccountApi {

    /**
     * 登录
     * @param userId
     * @param input
     * @param way 0 表示使用token，1 表示使用密码登录.
     * @param callback 收到返回消息调用回调方法
     */
    public static void login(String userId, String input, int way, IMSendCallback callback){

        // 封装信息
        String loginWay = way == 0 ? "token" : "password";
        JSONObject jsonObject = new JSONObject();
        jsonObject.put(loginWay, input);

        // 构建消息
        MessageProtobuf.Head head = MessageProtobuf.Head.newBuilder()
                .setMsgId(MsgIdGenerator.generateMsgId())
                .setMsgType(MessageType.HANDSHAKE.getMsgType())
                .setFromId(userId)
                .setExtend(jsonObject.toString())
                .build();

        MessageProtobuf.Msg msg = MessageProtobuf.Msg.newBuilder().setHead(head).build();

        Log.i("sim-account","发送消息\tmessage："+msg);
        IMSClientBootstrap.getInstance().sendMessage(msg, callback);
    }

    /**
     * 下线
     */
    public static void logout(String userId, IMSendCallback callback){
        // 封装信息
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("api", APITag.Logout);

        // 构建消息
        MessageProtobuf.Head head = MessageProtobuf.Head.newBuilder()
                .setMsgId(MsgIdGenerator.generateMsgId())
                .setMsgType(MessageType.HANDSHAKE.getMsgType())
                .setFromId(userId)
                .setExtend(jsonObject.toString())
                .build();

        MessageProtobuf.Msg msg = MessageProtobuf.Msg.newBuilder().setHead(head).build();

        // 发送下线通知
        Log.i("sim-account","发送消息\tmessage："+msg);
        IMSClientBootstrap.getInstance().sendMessage(msg, callback);
    }

    public static void register(String userId, String password, IMSendCallback callback){

        // 封装信息
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("api", APITag.Register);
        jsonObject.put("password", password);

        // 构建消息
        MessageProtobuf.Head head = MessageProtobuf.Head.newBuilder()
                .setMsgId(MsgIdGenerator.generateMsgId())
                .setMsgType(MessageType.REQUEST.getMsgType())
                .setFromId(userId)
                .setExtend(jsonObject.toString())
                .build();

        MessageProtobuf.Msg msg = MessageProtobuf.Msg.newBuilder().setHead(head).build();

        // 发送消息
        Log.i("sim-account","发送消息\tmessage："+msg);
        IMSClientBootstrap.getInstance().sendMessage(msg, callback);
    }

    public static void getPersonProfile(String userId, IMSendCallback callback){
        // 封装信息
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("api", APITag.GetPersonProfile);

        // 构建消息
        MessageProtobuf.Head head = MessageProtobuf.Head.newBuilder()
                .setMsgId(MsgIdGenerator.generateMsgId())
                .setMsgType(MessageType.REQUEST.getMsgType())
                .setFromId(userId)
                .setExtend(jsonObject.toString())
                .build();

        MessageProtobuf.Msg msg = MessageProtobuf.Msg.newBuilder().setHead(head).build();

        // 发送消息
        Log.i("sim-account","发送消息\tmessage："+msg);
        IMSClientBootstrap.getInstance().sendMessage(msg, callback);
    }

    public static void updatePersonProfile(String userId,String personJson, IMSendCallback callback){
        // 封装信息
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("api", APITag.UpdatePersonProfile);
        jsonObject.put("person", personJson);

        // 构建消息
        MessageProtobuf.Head head = MessageProtobuf.Head.newBuilder()
                .setMsgId(MsgIdGenerator.generateMsgId())
                .setFromId(userId)
                .setMsgType(MessageType.REQUEST.getMsgType())
                .setExtend(jsonObject.toString())
                .build();


        MessageProtobuf.Msg msg = MessageProtobuf.Msg.newBuilder().setHead(head).build();

        // 发送消息
        Log.i("sim-account","发送消息\tmessage："+msg);
        IMSClientBootstrap.getInstance().sendMessage(msg, callback);
    }
}
