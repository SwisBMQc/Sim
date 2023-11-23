package com.sy.im.util;

import android.util.Log;

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
        Log.i("sim-msg processor","收到消息:"+msg.getHead().getMsgType());


    }

}
