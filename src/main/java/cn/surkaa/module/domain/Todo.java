package cn.surkaa.module.domain;

import com.baomidou.mybatisplus.annotation.*;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;

import lombok.Data;

/**
 * @author SurKaa
 * @TableName todo
 */
@TableName(value = "todo")
@Data
public class Todo implements Serializable {

    @Serial
    @TableField(exist = false)
    private static final long serialVersionUID = -8372399403406137590L;

    /**
     * 主键
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 创建该笔记的用户id
     */
    private Long uid;

    /**
     * 是否完成这个笔记
     */
    private Integer flag;

    /**
     * 笔记标题
     */
    private String title;

    /**
     * 笔记细节
     */
    private String description;

    /**
     * 笔记状态 0 未完成 1 已经完成
     */
    private Integer noteStatus;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新时间
     */
    private Date updateTime;

    /**
     * 预计完成时间
     */
    private Date targetTile;

    /**
     * 是否被删除了
     */
    @TableLogic
    private Integer isDelete;
}