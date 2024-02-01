package com.sy.im.service.impl;

import com.sy.im.common.model.Group;
import com.sy.im.mapper.GroupMapper;
import com.sy.im.service.GroupService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author sy
 * @since 2023-12-09
 */
@Service
public class GroupServiceImp extends ServiceImpl<GroupMapper, Group> implements GroupService {

}
