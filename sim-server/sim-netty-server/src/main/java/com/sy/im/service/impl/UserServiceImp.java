package com.sy.im.service.impl;

import com.sy.im.common.model.User;
import com.sy.im.mapper.UserMapper;
import com.sy.im.service.UserService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 用户表 服务实现类
 * </p>
 *
 * @author sy
 * @since 2023-11-13
 */
@Service
public class UserServiceImp extends ServiceImpl<UserMapper, User> implements UserService {

}
