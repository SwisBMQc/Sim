package com.sy.im.message;

import android.util.Log;

import com.sy.im.interf.IMSendCallback;

import java.lang.ref.SoftReference;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import io.netty.util.internal.StringUtil;

/**
 * 消息管理器
 * 发送的消息保存到这里
 * 等待服务端对应的响应
 */
public class MessageManager {

    /**
     * key：msgId value：回调方法
     */
    private static final Map<String, SoftReference<IMSendCallback>> map = new ConcurrentHashMap<>();

    public static void put(String msgId, IMSendCallback callback) {
        Log.i("sim-mm","新消息"+msgId+"添加至管理器");
        map.put(msgId, new SoftReference<>(callback));
    }

    // 调用一次回调就移除
    public static IMSendCallback get(String msgId) {
        if (containsKey(msgId)){
            return map.remove(msgId).get();
        }
        return null;
    }

    public static boolean containsKey(String msgId) {
        return map.containsKey(msgId);
    }
}