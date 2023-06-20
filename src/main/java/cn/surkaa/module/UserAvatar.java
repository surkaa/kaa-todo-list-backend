package cn.surkaa.module;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * 头像表
 *
 * @author SurKaa
 * @TableName user_avatar
 */
@TableName(value = "user_avatar")
@Data
public class UserAvatar implements Serializable {

    @Serial
    @TableField(exist = false)
    private static final long serialVersionUID = -2427896973863486L;

    /**
     * 主键
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 二进制头像
     */
    private byte[] avatar;

}