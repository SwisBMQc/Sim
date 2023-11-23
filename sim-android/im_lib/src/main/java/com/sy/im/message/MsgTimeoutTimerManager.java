package com.sy.im.message;

import static com.sy.im.message.MessageType.HANDSHAKE;
import static com.sy.im.message.MessageType.HEARTBEAT;

import com.sy.im.interf.IMSClient;
import com.sy.im.protobuf.MessageProtobuf;

import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import io.netty.util.internal.StringUtil;

/**
 * 消息发送超时管理器，用于管理消息定时器的新增、移除等
 */
public class MsgTimeoutTimerManager {

    private Map<String, MsgTimeoutTimer> mMsgTimeoutMap = new ConcurrentHashMap<>();
    private IMSClient imsClient;// ims客户端

    public MsgTimeoutTimerManager(IMSClient imsClient) {
        this.imsClient = imsClient;
    }

    /**
     * 添加消息到发送超时管理器
     *
     * @param msg
     */
    public void add(MessageProtobuf.Msg msg) {
        if (msg == null || msg.getHead() == null) {
            return;
        }

        int msgType = msg.getHead().getMsgType();

        // 握手消息、心跳消息、客户端返回的状态报告消息，不用重发。
        if (msgType == HANDSHAKE.getMsgType() ||
                msgType == HEARTBEAT.getMsgType() ||
                msgType == MessageType.CLIENT_MSG_RECEIVED_STATUS_REPORT.getMsgType()) {
            return;
        }

        // 每个消息对应一个计时器
        String msgId = msg.getHead().getMsgId();
        if (!mMsgTimeoutMap.containsKey(msgId)) {
            MsgTimeoutTimer timer = new MsgTimeoutTimer(imsClient, msg);
            mMsgTimeoutMap.put(msgId, timer);
        }

        System.out.println("添加消息超发送超时管理器，message=" + msg + "\t当前管理器消息数：" + mMsgTimeoutMap.size());
    }

    /**
     * 从发送超时管理器中移除消息，并停止定时器
     *
     * @param msgId
     */
    public void remove(String msgId) {
        if (StringUtil.isNullOrEmpty(msgId)) {
            return;
        }

        MsgTimeoutTimer timer = mMsgTimeoutMap.remove(msgId);
        MessageProtobuf.Msg msg = null;
        if (timer != null) {
            msg = timer.getMsg();
            timer.cancel();
            timer = null;
        }

        System.out.println("从发送消息管理器移除消息，message=" + msg);
    }

    /**
     * 重连成功回调，重连并握手成功时，重发消息发送超时管理器中所有的消息
     */
    public synchronized void onResetConnected() {
        for(Iterator<Map.Entry<String, MsgTimeoutTimer>> it = mMsgTimeoutMap.entrySet().iterator(); it.hasNext();) {
            it.next().getValue().sendMsg();
        }
    }
}
