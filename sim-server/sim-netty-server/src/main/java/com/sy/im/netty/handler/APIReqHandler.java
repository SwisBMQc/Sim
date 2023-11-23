package com.sy.im.netty.handler;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.sy.im.common.constant.APITag;
import com.sy.im.common.constant.MessageType;
import com.sy.im.common.model.vo.Person;
import com.sy.im.common.protobuf.MessageProtobuf;
import com.sy.im.common.result.ResultJson;
import com.sy.im.netty.service.PersonService;
import com.sy.im.netty.util.ChannelHolder;
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
 * 处理API请求
 * @Author：sy
 * @Date：2023/11/20
 */
@Component("apiReqHandler")
@ChannelHandler.Sharable
public class APIReqHandler extends SimpleChannelInboundHandler<MessageProtobuf.Msg> {
    private final static Logger LOGGER = LoggerFactory.getLogger(APIReqHandler.class);

    @Autowired
    PersonService personService;

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, MessageProtobuf.Msg msg) throws Exception {
        LOGGER.info("收到消息："+msg);

        MessageProtobuf.Head head = msg.getHead();
        JSONObject extend = JSON.parseObject(head.getExtend());
        String fromId = head.getFromId();

        if (MessageType.REQUEST.getMsgType() == head.getMsgType()) {

            switch(extend.getString("api")){
                case APITag.Register:
                    String password = extend.getString("password");    //  取出密码
                    LOGGER.info("fromId："+fromId+"，请求注册...password："+password);

                    if (!StringUtils.isEmpty(password)){
                        ResultJson register = personService.register(fromId, password);
                        if (register.getStatus() == 1){    // 注册成功
                            ChannelHolder.put(fromId, (NioSocketChannel) ctx.channel());
                        }
                        responseMsg(ctx, msg, head, register);
                    }

                    break;

                case APITag.Logout:
                    LOGGER.info("fromId："+fromId+"，请求下线...");
                    ChannelHolder.remove(fromId, (NioSocketChannel) ctx.channel());
                    responseMsg(ctx, msg, head, ResultJson.success());

                    break;

                case APITag.GetPersonProfile:
                    LOGGER.info("fromId："+fromId+"，getPersonProfile...");
                    Person person = personService.getPerson(fromId);
                    responseMsg(ctx, msg, head, ResultJson.success().setData("person",person));

                    break;

                case APITag.UpdatePersonProfile:
                    LOGGER.info("fromId："+fromId+"，更新个人信息...");
                    Person person1 = JSONObject.parseObject(extend.getString("person"), Person.class);
                    ResultJson result = personService.updatePerson(person1);
                    responseMsg(ctx, msg, head, result);

                    break;


                default:{


                }
            }
        }else {
            ctx.fireChannelRead(msg);
        }
    }

    private void responseMsg(ChannelHandlerContext ctx, MessageProtobuf.Msg msg, MessageProtobuf.Head head, ResultJson resultJson) {
        LOGGER.info("API请求返回："+ resultJson.toString());
        head = head.toBuilder().setExtend(resultJson.toString()).build();
        msg = msg.toBuilder().setHead(head).build();
        ctx.writeAndFlush(msg);
    }
}