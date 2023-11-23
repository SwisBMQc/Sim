package com.sy.im.common.model;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import org.springframework.beans.factory.annotation.Value;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Date;

/**
 * <p>
 * 用户表
 * </p>
 *
 * @author sy
 * @since 2023-11-13
 */
@Data
public class User implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 用户ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 用户名，唯一索引
     */
    private String username;

    /**
     * 加密盐
     */
    private String salt;

    /**
     * md5加密
     */
    private String password;


    @TableLogic
    @TableField("is_deleted")
    private Integer isDeleted;

    /**
     * 注册时间
     */
    @TableField(fill = FieldFill.INSERT)
    private Date createTime;

}
