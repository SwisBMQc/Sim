package com.sy.im.netty.handler;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.sy.im.common.constant.MessageType;
import com.sy.im.common.protobuf.MessageProtobuf;
import com.sy.im.common.result.ResultJson;
import com.sy.im.common.util.JwtHelper;
import com.sy.im.netty.util.ChannelHolder;
import com.sy.im.netty.service.PersonService;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.socket.nio.NioSocketChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

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
        JSONObject jsonObj = JSON.parseObject(head.getExtend());

        String resultJson = "";

        if (MessageType.HANDSHAKE.getMsgType() == head.getMsgType()){
            String fromId = head.getFromId();
            String token = jsonObj.getString("token");
            String password = jsonObj.getString("password");

            LOGGER.info("fromId："+fromId+"，请求登录...token："+token+" / password："+password);

            // 使用token自动登录
            if (token != null && !StringUtils.isEmpty(token) && JwtHelper.getUsername(token).equals(fromId)){   // 校验成功
                ChannelHolder.put(fromId, (NioSocketChannel) ctx.channel());
                resultJson = ResultJson.success().toString();
            }
            // 使用password登录
            if (!StringUtils.isEmpty(password)){
                ResultJson login = personService.login(fromId, password);
                if (login.getStatus() == 1){    // 登录成功
                    ChannelHolder.put(fromId, (NioSocketChannel) ctx.channel());
                }
                resultJson = login.toString();
            }
            LOGGER.info("resultJson："+resultJson);
            head = head.toBuilder().setExtend(resultJson).build();
            msg = msg.toBuilder().setHead(head).build();
            ctx.writeAndFlush(msg);

        } else {
            ctx.fireChannelRead(msg);
        }
    }
}
