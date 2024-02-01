package com.sy.im.netty.handler;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.sy.im.common.constant.APITag;
import com.sy.im.common.constant.MessageType;
import com.sy.im.common.model.vo.Person;
import com.sy.im.common.result.ResultJson;
import com.sy.im.file.service.FileStorageService;
import com.sy.im.netty.service.FriendService;
import com.sy.im.netty.service.PersonService;
import com.sy.im.netty.util.ChannelHolder;
import com.sy.im.netty.util.MsgUtil;
import com.sy.im.protobuf.MessageProtobuf;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

/**
 * 处理API请求 个人信息
 * @Author：sy
 * @Date：2023/11/20
 */
@Component
@ChannelHandler.Sharable
public class APIReqHandler extends SimpleChannelInboundHandler<MessageProtobuf.Msg> {
    private final static Logger LOGGER = LoggerFactory.getLogger(APIReqHandler.class);

    @Autowired
    PersonService personService;
    @Autowired
    FileStorageService fileStorageService;
    @Autowired
    FriendService friendService;

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, MessageProtobuf.Msg msg) throws Exception {

        MessageProtobuf.Head head = msg.getHead();
        JSONObject extend = JSON.parseObject(head.getExtend());
        String fromId = head.getFromId();

        if (MessageType.REQUEST.getMsgType() == head.getMsgType()) {

            switch(extend.getString("api")){

                case APITag.HANDSHAKE:
                    LOGGER.info("fromId："+fromId+"，请求下线...");
                    ChannelHolder.remove(fromId, ctx.channel());
                    MsgUtil.respMsg(LOGGER,ctx, msg, head, ResultJson.success());

                    break;

                case APITag.GET_PERSON_INFO:
                    LOGGER.info("fromId："+fromId+"，getPersonProfile...");
                    ResultJson personInfo = personService.getPerson(extend.getString("userId"));
                    MsgUtil.respMsg(LOGGER,ctx, msg, head, personInfo);

                    break;

                case APITag.UPDATE_PERSON_INFO:
                    LOGGER.info("fromId："+fromId+"，更新个人信息...");
                    Person person1 = JSONObject.parseObject(extend.getString("person"), Person.class);
                    ResultJson result = personService.updatePerson(person1);
                    MsgUtil.respMsg(LOGGER,ctx, msg, head, result);

                    break;

                case APITag.UPLOAD_AVATAR:
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
                    MsgUtil.respMsg(LOGGER,ctx, msg, head, resultJson);

                    break;

                    /******************************* Friend *******************************/

                case APITag.FRIEND_REQUEST:
                    LOGGER.info("fromId："+fromId+"，好友申请...");
                    ResultJson requestResult = friendService.insertFriendRequest(fromId, head.getToId());
                    MsgUtil.respMsg(LOGGER,ctx, msg, head, requestResult);

                    break;

                case APITag.GET_FRIEND_REQUEST:
                    LOGGER.info("fromId："+fromId+"，获取好友申请列表...");
                    ResultJson requests = friendService.getFriendRequests(fromId);
                    MsgUtil.respMsg(LOGGER,ctx, msg, head, requests);

                    break;


                case APITag.UPDATE_FRIEND_REQUEST:
                    LOGGER.info("fromId："+fromId+"，更新好友验证...");
                    ResultJson resultJson1 = friendService.updateFriendRequest(extend.getLong("id"),extend.getIntValue("result") );
                    MsgUtil.respMsg(LOGGER,ctx, msg, head, resultJson1);

                    break;


                case APITag.GET_FRIEND_LIST:
                    LOGGER.info("fromId："+fromId+"，获取好友列表...");
                    ResultJson resultJson2 = friendService.getFriendLists(fromId);
                    MsgUtil.respMsg(LOGGER,ctx, msg, head, resultJson2);

                    break;

                default:

            }

        }else {
            ctx.fireChannelRead(msg);
        }
    }

}