package cn.surkaa.module.request.todo;

import lombok.Data;

/**
 * 修改预计完成时间的请求体
 *
 * @author SurKaa
 */
@Data
public class TodoTargetRequest {
    Long id;
    String target;
}
