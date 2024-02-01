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

public class AccountAPI {

    /**
     * 登录
     * @param userId 用户id
     * @param password 密码
     * @param callback 收到返回消息调用回调方法
     */
    public static void login(String userId,String password, IMSendCallback callback){

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

    public static void logout(IMSendCallback callback){
        // 封装信息
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("api", APITag.HANDSHAKE);

        requestMsg(MessageType.REQUEST, callback, jsonObject);
    }

    public static void getPersonInfo(String userId,IMSendCallback callback){
        // 封装信息
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("api", APITag.GET_PERSON_INFO);
        jsonObject.put("userId", userId);

        requestMsg(MessageType.REQUEST, callback, jsonObject);
    }

    public static void updatePersonProfile(String personJson, IMSendCallback callback){
        // 封装信息
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("api", APITag.UPDATE_PERSON_INFO);
        jsonObject.put("person", personJson);

        requestMsg(MessageType.REQUEST, callback, jsonObject);
    }

    /**
     * 上传头像
     * @param filename 文件名
     * @param imageBytes 图像的字节数组
     * @param callback 回调函数
     */
    public static void uploadProfile(String filename, byte[] imageBytes, IMSendCallback callback){

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("api", APITag.UPLOAD_AVATAR);
        jsonObject.put("filename", filename);

        // 构建消息
        MessageProtobuf.Head head = MessageProtobuf.Head.newBuilder()
                .setMsgId(MsgIdGenerator.generateMsgId())
                .setToken(MsgUtil.getToken())
                .setFromId(MsgUtil.getUserId())
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

    /**
     * 搜索
     * @param query 搜索条件
     * @param way 0：搜索全部，1：搜索用户，2：搜索群组
     * @param callback 回调方法
     */
    public static void search(String query, int way,IMSendCallback callback){
        // 封装信息
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("query", query);
        jsonObject.put("way", way);

        requestMsg(MessageType.HANDSHAKE, callback, jsonObject);
    }

}
