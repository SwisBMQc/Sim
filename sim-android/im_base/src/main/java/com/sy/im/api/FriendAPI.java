package com.sy.im.api;

import static com.sy.im.util.MsgUtil.requestMsg;

import com.alibaba.fastjson.JSONObject;
import com.sy.im.interf.IMSendCallback;
import com.sy.im.message.MessageType;
import com.sy.im.protobuf.MessageProtobuf;
import com.sy.im.util.APITag;

import org.jetbrains.annotations.NotNull;

/**
 * @Author：sy
 * @Date：2023/12/7
 */
public class FriendAPI {

    /**
     * 发送好友申请
     * @param friendId
     * @param callback
     */
    public static void sendFriendRequest(String friendId, IMSendCallback callback){
        // 封装信息
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("api", APITag.FRIEND_REQUEST);

        MessageProtobuf.Head.Builder headBuilder = MessageProtobuf.Head.newBuilder()
                .setToId(friendId)
                .setMsgType(MessageType.REQUEST.getMsgType())
                .setExtend(jsonObject.toString());

        requestMsg(headBuilder, callback);
    }

    /**
     * 获得好友申请列表
     * @param callback
     */
    public static void getFriendRequests(IMSendCallback callback){
        // 封装信息
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("api", APITag.GET_FRIEND_REQUEST);

        MessageProtobuf.Head.Builder headBuilder = MessageProtobuf.Head.newBuilder()
                .setMsgType(MessageType.REQUEST.getMsgType())
                .setExtend(jsonObject.toString());

        requestMsg(headBuilder, callback);
    }

    public static void getFriendList(@NotNull IMSendCallback imSendCallback) {
        // 封装信息
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("api", APITag.GET_FRIEND_LIST);

        MessageProtobuf.Head.Builder headBuilder = MessageProtobuf.Head.newBuilder()
                .setMsgType(MessageType.REQUEST.getMsgType())
                .setExtend(jsonObject.toString());

        requestMsg(headBuilder, imSendCallback);
    }

    public static void verifyFriend(long friendRequestId, int result, @NotNull IMSendCallback imSendCallback) {
        // 封装信息
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("api", APITag.UPDATE_FRIEND_REQUEST);
        jsonObject.put("id",friendRequestId);
        jsonObject.put("result",result);

        MessageProtobuf.Head.Builder headBuilder = MessageProtobuf.Head.newBuilder()
                .setMsgType(MessageType.REQUEST.getMsgType())
                .setExtend(jsonObject.toString());

        requestMsg(headBuilder, imSendCallback);
    }
}
