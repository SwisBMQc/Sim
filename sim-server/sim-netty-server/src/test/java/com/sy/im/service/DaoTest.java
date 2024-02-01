package com.sy.im.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.sy.im.ServerApplication;
import com.sy.im.common.model.FriendRequest;
import com.sy.im.common.model.UserInfo;
import com.sy.im.netty.service.FriendService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * @Author£ºsy
 * @Date£º2023/12/14
 */
@SpringBootTest(classes = ServerApplication.class)
@RunWith(SpringRunner.class)
public class DaoTest {

    @Autowired
    UserInfoService userInfoService;

    @Autowired
    FriendRequestService friendRequestService;

    @Autowired
    FriendService friendService;

    @Test
    public void test1(){
        UserInfo info = new UserInfo();
        info.setUserId(3L);
        info.setNickname("ÐÞ¹´");
        info.setImgUrl("https://sim-oss.oss-cn-guangzhou.aliyuncs.com/chat/avatar/sim_190549464.jpeg");
        LambdaQueryWrapper<UserInfo> wrapper = new LambdaQueryWrapper<UserInfo>().eq(UserInfo::getUserId, 3L);
        System.out.println(userInfoService.update(info, wrapper));
    }

    @Test
    public void testRequest(){

        FriendRequest request = friendRequestService.getById(1);
        request.setIsAgreed(1);
        friendRequestService.updateById(request);

    }

    @Test
    public void testFriend(){



    }

}
