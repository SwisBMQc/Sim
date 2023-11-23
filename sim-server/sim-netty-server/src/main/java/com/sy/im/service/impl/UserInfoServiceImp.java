package com.sy.im.service.impl;

import com.sy.im.common.model.UserInfo;
import com.sy.im.mapper.UserInfoMapper;
import com.sy.im.service.UserInfoService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author sy
 * @since 2023-11-14
 */
@Service
public class UserInfoServiceImp extends ServiceImpl<UserInfoMapper, UserInfo> implements UserInfoService {

}
