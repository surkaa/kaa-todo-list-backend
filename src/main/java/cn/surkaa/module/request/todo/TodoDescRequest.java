package cn.surkaa.module.request.todo;

import lombok.Data;

/**
 * 用于修改详细描述的请求体
 *
 * @author SurKaa
 */
@Data
public class TodoDescRequest {
    Long id;
    String description;
}
