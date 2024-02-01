package com.sy.im.common.model;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.ToString;

import java.io.Serializable;
import java.sql.Timestamp;
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
@TableName("friend_request")
public class FriendRequest implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    private Long fromId;

    private Long sendId;

    /**
     * 是否同意，0：未同意，1已同意，-1已拒绝
     */
    @TableField(fill = FieldFill.INSERT)
    private Integer isAgreed = 0;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Date updateTime;
}
