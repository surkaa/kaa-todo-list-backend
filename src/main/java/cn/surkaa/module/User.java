package cn.surkaa.module;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;

/**
 * 用户
 *
 * @author SurKaa
 * @TableName user
 */
@TableName(value = "user")
@Data
public class User implements Serializable {

    @Serial
    @TableField(exist = false)
    private static final long serialVersionUID = 3626636600845859345L;

    /**
     * 主键id
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 登录账号
     */
    private String userAccount;

    /**
     * 密码
     */
    private String userPassword;

    /**
     * 用户昵称
     */
    private String username;

    /**
     * 头像外键
     */
    private Long avatarId;

    /**
     * 性别
     */
    private Integer gender;

    /**
     * 电话
     */
    private String phone;

    /**
     * 邮箱
     */
    private String email;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新时间
     */
    private Date updateTime;

    /**
     * 用户状态 0 - 正常
     */
    private Integer userStatus;

    /**
     * 用户角色
     * <p>
     * <li>0 普通用户</li>
     * <li>1 管理员用户</li>
     */
    private Integer userRole;

    /**
     * 是否删除
     */
    @TableLogic
    private Integer isDelete;
}