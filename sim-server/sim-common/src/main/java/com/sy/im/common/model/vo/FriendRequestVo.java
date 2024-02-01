package com.sy.im.common.model.vo;

import lombok.Data;
import lombok.ToString;

import java.util.Date;

/**
 * <p>
 * 好友请求
 * </p>
 *
 * @author sy
 * @since 2023-12-15
 */
@Data
@ToString
public class FriendRequestVo {

    /**
     * 请求id
     */
    Long id;

    /**
     * 好友对象
     */
    Person friend;

    /**
     * 请求类型
     * 发送方还是接收方
     */
    Integer type;

    /**
     * 是否同意
     */
    Integer isAgreed;

    /**
     * 时间戳
     */
    Date updateTime;
}
