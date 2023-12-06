package com.sy.im.netty.util;

import com.sy.im.common.result.ResultJson;
import io.netty.channel.Channel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 *  channel连接成功，将用户登录状态与channel绑定
 */
public class ChannelHolder {

    private final static Logger LOGGER = LoggerFactory.getLogger(ChannelHolder.class);

    private static HashMap<String, Channel> sessionMap = new HashMap<>();

    public static void put(String username, Channel channel){
        sessionMap.put(username,channel);
    }

    public static void remove(String username, Channel channel){
        sessionMap.remove(username,channel);
    }
    public static Set<Map.Entry<String, Channel>> getEntrySet(){
        return sessionMap.entrySet();
    }

    public static boolean isLogin(String username) {
        return sessionMap.containsKey(username);
    }

    public static boolean isOnline(String username) {
        if (isLogin(username)){
            return sessionMap.get(username) == null;
        }
        return false;
    }
}
