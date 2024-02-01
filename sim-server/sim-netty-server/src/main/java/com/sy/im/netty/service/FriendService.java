package com.sy.im.netty.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.sy.im.common.model.Friend;
import com.sy.im.common.result.ResultJson;

/**
 * 好友相关逻辑
 * @Author：sy
 * @Date：2023/12/15
 */
public interface FriendService extends IService<Friend> {

    ResultJson insertFriendRequest(String fromId,String sendId);

    ResultJson getFriendRequests(String fromId);

    ResultJson updateFriendRequest(Long id, int result);

    ResultJson getFriendLists(String fromId);
}
