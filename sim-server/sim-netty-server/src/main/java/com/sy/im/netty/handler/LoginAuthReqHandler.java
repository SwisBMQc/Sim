package com.sy.im.netty.handler;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.sy.im.common.constant.MessageType;
import com.sy.im.common.result.ResultJson;
import com.sy.im.common.util.JwtHelper;
import com.sy.im.netty.util.ChannelHolder;
import com.sy.im.netty.service.PersonService;
import com.sy.im.protobuf.MessageProtobuf;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;

import static com.sy.im.netty.util.MsgUtil.authMsg;

/**
 * 登录认证处理
 */
@Component
@ChannelHandler.Sharable
public class LoginAuthReqHandler extends SimpleChannelInboundHandler<MessageProtobuf.Msg> {

    private final static Logger LOGGER = LoggerFactory.getLogger(LoginAuthReqHandler.class);

    @Autowired
    PersonService personService;

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        LOGGER.info("channelActive："+ctx.channel().remoteAddress()+"连接成功");
        super.channelActive(ctx);
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, MessageProtobuf.Msg msg) throws Exception {
        MessageProtobuf.Head head = msg.getHead();
        String fromId = head.getFromId();
        JSONObject jsonObj = JSON.parseObject(head.getExtend());

        if (MessageType.HANDSHAKE.getMsgType() == head.getMsgType()){
            String password = jsonObj.getString("password");
            int way = jsonObj.getIntValue("way");
            ResultJson resultJson;

            if (way == 0){
                LOGGER.info("fromId："+fromId+"，请求登录");
                resultJson = personService.login(fromId, password);
            }else {
                LOGGER.info("fromId："+fromId+"，请求注册");
                resultJson = personService.register(fromId, password);
            }

            if (resultJson.getStatus() == 1){ // 操作成功
                ChannelHolder.put(fromId, ctx.channel());
            }
            authMsg(LOGGER,ctx, msg, head, resultJson);

        }
        else {
            // 登录予以放行
            if (JwtHelper.getUsername(head.getToken()).equals(head.getFromId())){
                ChannelHolder.put(fromId, ctx.channel());
                ctx.fireChannelRead(msg);
            } else {
                MessageProtobuf.Head auth = head.toBuilder().setMsgType(MessageType.HANDSHAKE.getMsgType()).build();
                authMsg(LOGGER,ctx, msg, auth, ResultJson.error("未登录"));
            }

        }

    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        String username = "";
        for (Map.Entry<String, Channel> map : ChannelHolder.getEntrySet()) {
            if (map.getValue() == ctx.channel()) {
                username = map.getKey();
                break;
            }
        }
        ChannelHolder.put(username, null);
    }

}
