package com.sy.im.util;

import android.util.Log;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.sy.im.message.MessageManager;
import com.sy.im.message.MessageType;
import com.sy.im.protobuf.MessageProtobuf;

/**
 * 消息处理器
 */
public class MessageProcessor {

    private static final MessageProcessor INSTANCE = new MessageProcessor();

    private MessageProcessor() {}

    public static MessageProcessor getInstance() {
        return INSTANCE;
    }

    public void receiveMsg(final MessageProtobuf.Msg msg) {
        if (msg == null || msg.getHead() == null) {
            return;
        }

        Log.i("sim-msg processor","收到消息:"+msg);

        if (MessageType.REQUEST.getMsgType() == msg.getHead().getMsgType()) {
            int status = -1;
            String reason = "";

            try {
                // 从消息中获得状态
                JSONObject resultJson = JSON.parseObject(msg.getHead().getExtend());
                status = resultJson.getIntValue("status");
                reason = resultJson.getString("reason");

            } finally {
                if (status == 1) {
                    MessageManager.get(msg.getHead().getMsgId()).onSuccess(msg);
                } else {
                    MessageManager.get(msg.getHead().getMsgId()).onError(reason);
                }
            }
        }

    }

}
