package cn.surkaa.module.request.todo;

import lombok.Data;

/**
 * 更新todo 标记完成与否
 *
 * @author SurKaa
 */
@Data
public class TodoFlagRequest {
    Long id;
    Boolean flag;
}
