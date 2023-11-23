package com.sy.im.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.sy.im.common.model.User;
import com.sy.im.common.model.UserInfo;
import com.sy.im.common.model.vo.Person;
import com.sy.im.common.result.ResultJson;
import com.sy.im.common.util.JwtHelper;
import com.sy.im.common.util.SaltGenerator;
import com.sy.im.netty.service.PersonService;
import com.sy.im.service.UserInfoService;
import com.sy.im.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import java.util.Date;

/**
 * @Author：sy
 * @Date：2023/11/14
 */
@Service
public class PersonServiceImpl implements PersonService {

    private final static Logger LOGGER = LoggerFactory.getLogger(PersonServiceImpl.class);

    @Autowired
    UserService userService;

    @Autowired
    UserInfoService userInfoService;

    @Override
    public ResultJson login(String username, String password) {
        LOGGER.info("PersonServiceImpl username："+username);
        User user = userService.getOne(new LambdaQueryWrapper<User>().eq(User::getUsername, username));
        if (user == null){
            return ResultJson.error("查无此人");
        }

        String salt = user.getSalt();
        String pwd = DigestUtils.md5DigestAsHex((password + salt).getBytes()); // spring框架提供的工具类
        if (!pwd.equals(user.getPassword())){
            return ResultJson.error("密码错误");
        }

        Person person = getPerson(username);

        String token = JwtHelper.createToken(username);
        ResultJson resultJson = ResultJson.success("登录成功")
                .setData("token", token)
                .setData("person", person);

        return resultJson;
    }

    @Override
    public ResultJson register(String username, String password) {
        User one = userService.getOne(new LambdaQueryWrapper<User>().eq(User::getUsername, username));
        if (one != null){
            return ResultJson.error("userid 重复");
        }

        User user = new User();
        Person person = new Person();
        String salt = SaltGenerator.generateSalt();       // 生成盐
        String pwd = DigestUtils.md5DigestAsHex((password + salt).getBytes());  // MD5加密
        user.setUsername(username);
        user.setSalt(salt);
        user.setPassword(pwd);
        user.setCreateTime(new Date());

        if (userService.save(user)) {
            person.setUserId(username);
            String token = JwtHelper.createToken(username);

            UserInfo userInfo = new UserInfo();
            userInfo.setUserId(user.getId());
            userInfoService.save(userInfo);

            LOGGER.error(username+"注册成功");
            return ResultJson.success("注册成功")
                    .setData("token", token)
                    .setData("person", person);
        } else {
            LOGGER.error(username+"注册失败");
            return ResultJson.error("注册失败");
        }
    }

    @Override
    public ResultJson updatePerson(Person person) {
        User one = userService.getOne(new LambdaQueryWrapper<User>().eq(User::getUsername, person.getUserId()));
        // 查询条件
        LambdaQueryWrapper<UserInfo> wrapper = new LambdaQueryWrapper<UserInfo>().eq(UserInfo::getUserId, one.getId());

        UserInfo info = userInfoService.getOne(wrapper);
        BeanUtils.copyProperties(person,info);

        if (userInfoService.update(info, wrapper)) {
            return ResultJson.success();
        }

        return ResultJson.error("修改失败");
    }

    @Override
    public Person getPerson(String username) {
        User one = userService.getOne(new LambdaQueryWrapper<User>().eq(User::getUsername, username));
        UserInfo userInfo = userInfoService.getOne(new LambdaQueryWrapper<UserInfo>().eq(UserInfo::getUserId, one.getId()));

        Person person = new Person();
        if (userInfo != null){
            BeanUtils.copyProperties(userInfo,person);
        }
        person.setUserId(username); // 注意 Person的userId是username, user_info中的user_id是user表中的id
        LOGGER.info("getPerson："+person);

        return person;
    }



}
