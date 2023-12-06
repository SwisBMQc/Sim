package com.sy.im.netty.handler;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.mysql.cj.x.protobuf.MysqlxDatatypes;
import com.sy.im.common.constant.APITag;
import com.sy.im.common.constant.MessageType;
import com.sy.im.common.model.vo.Person;
import com.sy.im.common.result.ResultJson;
import com.sy.im.file.service.FileStorageService;
import com.sy.im.netty.service.PersonService;
import com.sy.im.netty.util.ChannelHolder;
import com.sy.im.protobuf.MessageProtobuf;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.Base64;

import static com.sy.im.netty.util.MsgUtil.authMsg;

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

    @Autowired
    FileStorageService fileStorageService;

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, MessageProtobuf.Msg msg) throws Exception {

        MessageProtobuf.Head head = msg.getHead();
        JSONObject extend = JSON.parseObject(head.getExtend());
        String fromId = head.getFromId();

        if (MessageType.REQUEST.getMsgType() == head.getMsgType()) {

            switch(extend.getString("api")){

                case APITag.Logout:
                    LOGGER.info("fromId："+fromId+"，请求下线...");
                    ChannelHolder.remove(fromId, ctx.channel());
                    authMsg(LOGGER,ctx, msg, head, ResultJson.success());

                    break;

                case APITag.GetPersonProfile:
                    LOGGER.info("fromId："+fromId+"，getPersonProfile...");
                    Person person = personService.getPerson(fromId);
                    authMsg(LOGGER,ctx, msg, head, ResultJson.success().setData("person",person));

                    break;

                case APITag.UpdatePersonProfile:
                    LOGGER.info("fromId："+fromId+"，更新个人信息...");
                    Person person1 = JSONObject.parseObject(extend.getString("person"), Person.class);
                    ResultJson result = personService.updatePerson(person1);
                    authMsg(LOGGER,ctx, msg, head, result);

                    break;

                case APITag.UploadProfile:
                    LOGGER.info("fromId："+fromId+"，上传头像...");
                    String filename = extend.getString("filename");
                    ResultJson resultJson;
                    try {
                        byte[] base64Image = msg.getBody().toByteArray();
                        if (!StringUtils.isEmpty(base64Image)){
                            String filePath = fileStorageService.uploadAvatar(fromId, filename, base64Image);
                            resultJson = ResultJson.success().setData("filePath",filePath);
                        } else {
                            throw new RuntimeException("上传数据为空");
                        }
                    } catch (Exception e) {
                        resultJson = ResultJson.error("上传失败"+e.getMessage());
                    }
                    authMsg(LOGGER,ctx, msg, head, resultJson);

                default:{


                }
            }
        }else {
            ctx.fireChannelRead(msg);
        }
    }

}