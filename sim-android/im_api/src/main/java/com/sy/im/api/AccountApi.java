package com.sy.im.api;

import static com.sy.im.util.MsgUtil.requestMsg;

import android.util.Log;

import com.alibaba.fastjson.JSONObject;
import com.google.protobuf.ByteString;
import com.sy.im.client.IMSClientBootstrap;
import com.sy.im.interf.IMSendCallback;
import com.sy.im.message.MessageType;
import com.sy.im.protobuf.MessageProtobuf;
import com.sy.im.util.APITag;
import com.sy.im.util.MsgIdGenerator;
import com.sy.im.util.MsgUtil;

import java.util.Base64;

public class AccountApi {



    /**
     * 登录
     * @param userId
     * @param password
     * @param callback 收到返回消息调用回调方法
     */
    public static void login(String userId, String password, IMSendCallback callback){

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("password", password);
        jsonObject.put("way", 0);

        requestMsg(userId,MessageType.HANDSHAKE, callback, jsonObject);
    }

    public static void register(String userId, String password, IMSendCallback callback){

        // 封装信息
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("password", password);
        jsonObject.put("way", 1);

        requestMsg(userId,MessageType.HANDSHAKE, callback, jsonObject);
    }

    /**
     * 下线
     */
    public static void logout(String userId, IMSendCallback callback){
        // 封装信息
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("api", APITag.Logout);

        requestMsg(userId,MessageType.REQUEST, callback, jsonObject);
    }

    public static void getPersonProfile(String userId, IMSendCallback callback){
        // 封装信息
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("api", APITag.GetPersonProfile);

        requestMsg(userId,MessageType.REQUEST, callback, jsonObject);
    }

    public static void updatePersonProfile(String userId,String personJson, IMSendCallback callback){
        // 封装信息
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("api", APITag.UpdatePersonProfile);
        jsonObject.put("person", personJson);

        requestMsg(userId,MessageType.REQUEST, callback, jsonObject);
    }

    /**
     * 上传头像
     * @param userId 用户名
     * @param filename 文件名
     * @param imageBytes 图像的字节数组
     * @param callback 回调函数
     */
    public static void uploadProfile(String userId,String filename, byte[] imageBytes, IMSendCallback callback){

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("api", APITag.UploadProfile);
        jsonObject.put("filename", filename);

        // 构建消息
        MessageProtobuf.Head head = MessageProtobuf.Head.newBuilder()
                .setMsgId(MsgIdGenerator.generateMsgId())
                .setToken(MsgUtil.getToken())
                .setFromId(userId)
                .setMsgType(MessageType.REQUEST.getMsgType())
                .setMsgContentType(MessageType.MessageContentType.IMAGE.getMsgContentType())
                .setExtend(jsonObject.toString())
                .build();

        MessageProtobuf.Msg msg = MessageProtobuf.Msg.newBuilder()
                .setHead(head)
                .setBody(ByteString.copyFrom(imageBytes))
                .build();

        // 发送消息
        Log.i("sim-account","发送消息\tmessage："+msg);
        IMSClientBootstrap.getInstance().sendMessage(msg, callback);
    }






}
