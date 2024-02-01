package com.sy.im.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.sy.im.common.model.Friend;
import com.sy.im.common.model.FriendRequest;
import com.sy.im.common.model.User;
import com.sy.im.common.model.UserInfo;
import com.sy.im.common.model.vo.FriendRequestVo;
import com.sy.im.common.model.vo.Person;
import com.sy.im.common.result.ResultJson;
import com.sy.im.mapper.FriendMapper;
import com.sy.im.netty.service.FriendService;
import com.sy.im.service.FriendRequestService;
import com.sy.im.service.UserInfoService;
import com.sy.im.service.UserService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @Author：sy
 * @Date：2023/12/15
 */
@Service
public class FriendServiceImp extends ServiceImpl<FriendMapper, Friend> implements FriendService {

    @Autowired
    UserService userService;
    @Autowired
    FriendRequestService friendRequestService;

    @Autowired
    UserInfoService userInfoService;

    @Override
    public ResultJson insertFriendRequest(String fromId, String sendId) {

        Long fromUserId = getUserId(fromId);
        Long sendUserId = getUserId(sendId);

        LambdaQueryWrapper<FriendRequest> wrapper = new LambdaQueryWrapper<FriendRequest>()
                .eq(FriendRequest::getFromId, fromUserId)
                .eq(FriendRequest::getSendId, sendUserId);

        FriendRequest one = friendRequestService.getOne(wrapper);
        if (one != null){   // 已存在
            String status;
            if (one.getIsAgreed() == 1){
                status = "已同意";
            } else if (one.getIsAgreed() == -1) {
                status = "已拒绝";
            } else {
                status = "未同意";
            }
            return ResultJson.error("重复请求，"+status);
        }

        FriendRequest request = new FriendRequest();
        request.setFromId(fromUserId);
        request.setSendId(sendUserId);
        if(!friendRequestService.save(request)){
            return ResultJson.error("保存失败");
        }
        return ResultJson.success();
    }

    @Override
    public ResultJson getFriendRequests(String fromId) {

        Long userId = getUserId(fromId);
        List<FriendRequest> requests = friendRequestService.list(new LambdaQueryWrapper<FriendRequest>()
                .eq(FriendRequest::getFromId, userId)
                .or().eq(FriendRequest::getSendId, userId));

        List<FriendRequestVo> friendRequestVos = requests.stream().map(res -> {

            FriendRequestVo vo = new FriendRequestVo();
            BeanUtils.copyProperties(res,vo);

            if (res.getFromId() == userId){ // 请求方
                vo.setType(0);
                Person friend = getPerson(res.getSendId());
                vo.setFriend(friend);
            } else {  // 接收方
                vo.setType(1);
                Person friend = getPerson(res.getFromId());
                vo.setFriend(friend);
            }

            return vo;
        }).collect(Collectors.toList());

        return ResultJson.success("userId: "+ userId).setData("requests",friendRequestVos);
    }

    @Override
    public ResultJson updateFriendRequest(Long requestId, int result) {
        FriendRequest request = friendRequestService.getById(requestId);
        request.setIsAgreed(result);
        friendRequestService.updateById(request);

        if (result == 1){
            Friend user = new Friend();
            user.setUserId(request.getFromId());
            user.setFriendId(request.getSendId());
            Friend friend = new Friend();
            friend.setUserId(request.getSendId());
            friend.setFriendId(request.getFromId());
            this.save(user);
            this.save(friend);
        }

        return ResultJson.success();
    }

    @Override
    public ResultJson getFriendLists(String fromId) {

        Long userId = getUserId(fromId);
        List<Friend> friends = this.list(new LambdaQueryWrapper<Friend>().eq(Friend::getUserId, userId));
        List<Person> friendVos = friends.stream().map( i -> {
            return getPerson(i.getFriendId());
        }).collect(Collectors.toList());

        return ResultJson.success("获取好友列表").setData("friends",friendVos);
    }


    Long getUserId(String username){
        return userService.getOne(new LambdaQueryWrapper<User>().eq(User::getUsername, username)).getId();
    }


    Person getPerson(Long userId){
        String username = userService.getOne(new LambdaQueryWrapper<User>().eq(User::getId, userId)).getUsername();
        UserInfo userInfo = userInfoService.getOne(new LambdaQueryWrapper<UserInfo>().eq(UserInfo::getUserId, userId));

        Person person = new Person();
        if (userInfo != null){
            BeanUtils.copyProperties(userInfo,person);
        }
        person.setUserId(username);
        return person;
    }
}
