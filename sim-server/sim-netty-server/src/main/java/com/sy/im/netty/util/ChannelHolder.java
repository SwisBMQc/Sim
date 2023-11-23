package com.sy.im.netty.util;

import com.sy.im.netty.handler.LoginAuthReqHandler;
import io.netty.channel.Channel;
import io.netty.channel.socket.nio.NioSocketChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;

/**
 *  channel连接成功，将用户登录状态与channel绑定
 */
public class ChannelHolder {

    private final static Logger LOGGER = LoggerFactory.getLogger(ChannelHolder.class);

    /**
     *  服务器channel列表
     */
    private static HashMap<String, NioSocketChannel> ChannelList = new HashMap<>();

    public static void put(String username, NioSocketChannel channel){
        LOGGER.info("ChannelHolder.put "+username+" , "+channel+", ChannelList："+ChannelList);
        setUserName(username,channel);
        setLogin(channel);
        ChannelList.put(username,channel);
    }

    public static void remove(String username, NioSocketChannel channel){
        LOGGER.info("remove： "+username);
        ChannelList.remove(username,channel);
    }

    public static void setLogin(Channel channel) {
        channel.attr(State.LOGIN).set(true);
    }

    public static boolean isLogin(Channel channel) {
        return channel.hasAttr(State.LOGIN);
    }

    public static String getUserName(Channel channel) {
        return channel.attr(State.USERNAME).get();
    }

    public static void setUserName(String id,Channel channel) {
        channel.attr(State.USERNAME).set(id);
    }
}
